package cn.iocoder.yudao.module.data.service.inputexec;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.data.dal.dataobject.dspslotday.DspSlotDayDO;
import cn.iocoder.yudao.module.data.dal.dataobject.sspslotday.SspSlotDayDO;
import cn.iocoder.yudao.module.data.dal.mysql.dspslotday.DspSlotDayMapper;
import cn.iocoder.yudao.module.data.dal.mysql.sspslotday.SspSlotDayMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import cn.iocoder.yudao.module.data.controller.admin.inputexec.vo.*;
import cn.iocoder.yudao.module.data.dal.dataobject.inputexec.InputExecDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.data.dal.mysql.inputexec.InputExecMapper;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.INPUT_EXEC_NOT_EXISTS;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;


/**
 * DSP数据导入 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class InputExecServiceImpl implements InputExecService {

    @Resource
    private InputExecMapper inputExecMapper;

    @Resource
    private SspSlotDayMapper sspSlotDayMapper;

    @Resource
    private DspSlotDayMapper dspSlotDayMapper;


    @Override
    public Long createInputExec(InputExecSaveReqVO createReqVO) {
        // 插入
        InputExecDO inputExec = BeanUtils.toBean(createReqVO, InputExecDO.class);
        inputExecMapper.insert(inputExec);

        // 返回
        return inputExec.getId();
    }

    @Override
    public void updateInputExec(InputExecSaveReqVO updateReqVO) {
        // 校验存在
        validateInputExecExists(updateReqVO.getId());
        // 更新
        InputExecDO updateObj = BeanUtils.toBean(updateReqVO, InputExecDO.class);
        inputExecMapper.updateById(updateObj);
    }

    @Override
    public void deleteInputExec(Long id) {
        // 校验存在
        validateInputExecExists(id);
        // 删除
        inputExecMapper.deleteById(id);
    }

    @Override
        public void deleteInputExecListByIds(List<Long> ids) {
        // 删除
        inputExecMapper.deleteByIds(ids);
        }


    private void validateInputExecExists(Long id) {
        if (inputExecMapper.selectById(id) == null) {
            throw exception(INPUT_EXEC_NOT_EXISTS);
        }
    }

    @Override
    public InputExecDO getInputExec(Long id) {
        return inputExecMapper.selectById(id);
    }

    @Override
    public PageResult<InputExecDO> getInputExecPage(InputExecPageReqVO pageReqVO) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<InputExecDO> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<InputExecDO> resultPage = inputExecMapper.selectPage(page, pageReqVO);
        return new PageResult<>(resultPage.getRecords(), resultPage.getTotal());
    }

    @Override
    public List<InputExecDO> getDownExcelInput(Long id,String inputTime) {
        return inputExecMapper.getDownExcelInput(id, inputTime);
    }

    @Override
    public String importExecList(List<InputExecTemplateVO> list, Long companyId) {
        if (CollUtil.isEmpty(list)) {
            return "导入数据为空";
        }
        // 报表整理集合对象

        // 1 将导入的数据，按照dspSlotCode 分成几分
        List<ListInputExecArr> listInputExecArr = new ArrayList<>();
        Map<String, List<InputExecTemplateVO>> groupMap = new LinkedHashMap<>();
        for (InputExecTemplateVO vo : list) {
            String key = vo.getDspSlotCode();
            groupMap.computeIfAbsent(key, k -> new ArrayList<>()).add(vo);
        }
        for (Map.Entry<String, List<InputExecTemplateVO>> entry : groupMap.entrySet()) {
            ListInputExecArr arr = new ListInputExecArr();
            arr.setDspSlotCode(entry.getKey());
            if (entry.getValue().get(0).getSpend() == 0) {
             continue;
            }
            arr.setSpend(entry.getValue().get(0).getSpend() * 100);
            arr.setListInputExecTemplateVO(entry.getValue());
            listInputExecArr.add(arr);
        }
        // 遍历收集的集合

       for (ListInputExecArr arr : listInputExecArr) {
           String dspSlotCode = arr.getDspSlotCode();
           if (arr.getSpend() == 0) {
               continue;  // 排除收益是0的数据
           }
           // 遍历媒体数据
           // 1 获取所有的曝光次数
           // 2 算出每个媒体的曝光占比
           // 3 将收益分出
           // 4 入库
           Long sumShow = 0L;
           for (InputExecTemplateVO listInputExec : arr.getListInputExecTemplateVO()) {

               Long sspSlotId = listInputExec.getSspSlotId();
               String inputTime = listInputExec.getInputTime();
               SspSlotDayDO sspSlotDayDO = sspSlotDayMapper.selectSspSlotId(sspSlotId,dspSlotCode, inputTime);
               System.out.println(sspSlotDayDO);
               listInputExec.setShow(sspSlotDayDO.getShowPv());
               sumShow += sspSlotDayDO.getShowPv();
           }
            System.out.println("== sumShow = " + sumShow);
           for (InputExecTemplateVO listInputExec : arr.getListInputExecTemplateVO()) {
              if (listInputExec.getShow() == 0) {
                  listInputExec.setPercentage(0.0);
                  continue;
              }
              double Percentage = (double) listInputExec.getShow() / sumShow * 100;
              listInputExec.setPercentage(Percentage);
           }

           for (InputExecTemplateVO listInputExec : arr.getListInputExecTemplateVO()) {

               Long sspSlotId = listInputExec.getSspSlotId();
               String inputTime = listInputExec.getInputTime();
               if (listInputExec.getShow() == 0 || listInputExec.getPercentage()== 0) {
                   continue;
               }
               // 这个是我方的收益 =  金额 X 曝光比例 / 100
               Long spend = (long) (arr.getSpend() * listInputExec.getPercentage() / 100);
               if (spend == 0) {
                   continue;
               }
               Long income = (long) (spend * listInputExec.getSettleRate() / 100);
               SspSlotDayDO sspSlotDayDO = new SspSlotDayDO();
               sspSlotDayDO.setSspSlotId(sspSlotId);
               sspSlotDayDO.setDspSlotCode(dspSlotCode);
               // inputTime 格式 "2026-10-10" → 转为 20261010
               String dateStr = listInputExec.getInputTime().replace("-", "");
               sspSlotDayDO.setDate(Long.valueOf(dateStr));
               sspSlotDayDO.setIncome(income);
               sspSlotDayDO.setSpend(spend);
               sspSlotDayMapper.updateSspSlotDay(sspSlotDayDO);
               // 修改dspSlotDayMapper
               DspSlotDayDO dspSlotDayDO = new DspSlotDayDO();
               dspSlotDayDO.setSspSlotId(sspSlotId);
               dspSlotDayDO.setDspSlotCode(dspSlotCode);
               dspSlotDayDO.setDate(Long.valueOf(dateStr));
               dspSlotDayDO.setIncome(income);
               dspSlotDayDO.setSpend(spend);
               dspSlotDayMapper.updateSsspSlotDay(dspSlotDayDO);
           }
//           InputExecDO inputExecDO = new InputExecDO();
//           inputExecDO.setId(arr.getListInputExecTemplateVO().get(0).getId());
//           // inputTime 格式 "2026-10-10" → 转为 20261010
//           inputExecDO.setInputTime(arr.getListInputExecTemplateVO().get(0).getInputTime());
//           inputExecDO.setUpdateTime(LocalDateTime.now());
//           inputExecMapper.updateInputExec(inputExecDO);
       }
        return "导入成功";

        // 然后逐个去计算导入收益


//        int successCount = 0;
//        int failCount = 0;
//        for (InputExecTemplateVO vo : list) {
//            try {
//                InputExecDO exec = new InputExecDO();
//                exec.setCompanyId(companyId);
//                exec.setTables(1L);
//                exec.setInputTime(null);
//                exec.setSspSlotId(vo.getSspSlotId());
//                exec.setDspSlotCode(vo.getDspSlotCode());
//                exec.setMediaCompany(vo.getMediaCompany());
//                inputExecMapper.insert(exec);
//                successCount++;
//            } catch (Exception e) {
//                failCount++;
//            }
//        }
//        return "导入完成，成功 " + successCount + " 条，失败 " + failCount + " 条";
    }

}