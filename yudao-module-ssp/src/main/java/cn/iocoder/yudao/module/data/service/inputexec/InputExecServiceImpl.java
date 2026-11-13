package cn.iocoder.yudao.module.data.service.inputexec;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.module.data.dal.dataobject.dspslotday.DspSlotDayDO;
import cn.iocoder.yudao.module.data.dal.mysql.dspslotday.DspSlotDayMapper;
import cn.iocoder.yudao.module.data.dal.mysql.sspslotday.SspSlotDayMapper;
import cn.iocoder.yudao.module.dsp.dal.dataobject.dspslotinfo.DspSlotInfoDO;
import cn.iocoder.yudao.module.dsp.dal.mysql.dspslotinfo.DspSlotInfoMapper;
import cn.iocoder.yudao.module.ssp.dal.dataobject.sspSlotInfo.SspSlotInfoDO;
import cn.iocoder.yudao.module.ssp.dal.mysql.sspSlotInfo.SspSlotInfoMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.math.BigDecimal;
import cn.iocoder.yudao.module.data.controller.admin.inputexec.vo.*;
import cn.iocoder.yudao.module.data.dal.dataobject.inputexec.InputExecDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.data.dal.mysql.inputexec.InputExecMapper;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.INPUT_EXEC_NOT_EXISTS;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;


/**
 * DSP鏁版嵁瀵煎叆 Service 瀹炵幇绫? *
 * @author 鑺嬮亾婧愮爜
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
    @Autowired
    private SspSlotInfoMapper sspSlotInfoMapper;
    @Autowired
    private DspSlotInfoMapper dspSlotInfoMapper;


    @Override
    public Long createInputExec(InputExecSaveReqVO createReqVO) {
        // 鎻掑叆
        InputExecDO inputExec = BeanUtils.toBean(createReqVO, InputExecDO.class);
        inputExecMapper.insert(inputExec);

        // 杩斿洖
        return inputExec.getId();
    }

    @Override
    public void updateInputExec(InputExecSaveReqVO updateReqVO) {
        // 鏍￠獙瀛樺湪
        validateInputExecExists(updateReqVO.getId());
        // 鏇存柊
        InputExecDO updateObj = BeanUtils.toBean(updateReqVO, InputExecDO.class);
        inputExecMapper.updateById(updateObj);
    }

    @Override
    public void deleteInputExec(Long id) {
        // 鏍￠獙瀛樺湪
        validateInputExecExists(id);
        // 鍒犻櫎
        inputExecMapper.deleteById(id);
    }

    @Override
        public void deleteInputExecListByIds(List<Long> ids) {
        // 鍒犻櫎
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
        Page<InputExecDO> page = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        Page<InputExecDO> resultPage = inputExecMapper.selectPage(page, pageReqVO);
        return new PageResult<>(resultPage.getRecords(), resultPage.getTotal());
    }

    @Override
    public List<InputExecDO> getDownExcelInput(Long id,String inputTime) {
        return inputExecMapper.getDownExcelInput(id, inputTime);
    }

    @Override
    public List<DspSlotDayInputRespVo> importExecList(List<InputExecTemplateVO> list, Long id) {
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        InputExecDO inputExecDO = inputExecMapper.selectInputExecId(id);
        if (inputExecDO == null) {
            throw new IllegalArgumentException("导入数据ID查询不到数据，请联系管理员");
        }
        // 用companyId 公司id去查找dspSlotInfo预算能查到数据
        for (InputExecTemplateVO inputExecTemplateVO : list) {

            DspSlotInfoDO dspSlotInfoDO = dspSlotInfoMapper.selectDspSlotCodeAndCompanyId(inputExecTemplateVO.getDspSlotCode(), inputExecDO.getCompanyId());
            if (dspSlotInfoDO == null) {
                // 校验失败立即终止导入，由控制器将该消息返回给前端。
                throw new IllegalArgumentException("导入预算方广告位和公司有差异");
            }
        }

        for (InputExecTemplateVO inputExecTemplateVO1 : list) {
            try {
                LocalDate localDate = LocalDate.parse(inputExecTemplateVO1.getInputTime());
                String inputTimeResult = localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            } catch (DateTimeParseException e) {
                // 日期格式不正确或者日期不存在
                throw new IllegalArgumentException("检查导入日期");
            }

        }

        // 拦击日期和预算位CODE 重复的两条/多条数据
        Set<List<String>> inputTimeAndDspCodeSet = new HashSet<>();
        for (InputExecTemplateVO inputExecTemplateVO1 : list) {
            String inputTime = inputExecTemplateVO1.getInputTime();
            String dspCode = inputExecTemplateVO1.getDspSlotCode();

            if (!inputTimeAndDspCodeSet.add(Arrays.asList(inputTime, dspCode))) {
                throw new IllegalArgumentException("导入数据中日期和预算广告位ID不能重复");
            }

        }


        // 鍒涘缓杩斿洖瀵硅薄
        List<DspSlotDayInputRespVo> dspSlotInfoInputRespVo = new ArrayList<>();

        // 1 瀵煎叆浠ュ悗鍏堟敹闆嗗獟浣撲俊鎭?        // 閬嶅巻瀵煎叆棰勭畻骞垮憡浣岻D
        for (InputExecTemplateVO inputExecTemplateVO : list) {
            if (inputExecTemplateVO.getProfit() != null && inputExecTemplateVO.getProfit().signum() != 0) {
                inputExecTemplateVO.setProfit(inputExecTemplateVO.getProfit().multiply(BigDecimal.valueOf(100000))); // 灏嗗厓杞寲鎴愬垎
            }
            String inputTime = inputExecTemplateVO.getInputTime();
            String dspSlotCode = inputExecTemplateVO.getDspSlotCode();
            LocalDate localDate = LocalDate.parse(inputTime);
            String inputTimeResult = localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            List<DspSlotDayDO> dspSlotDayDOS = dspSlotDayMapper.selectDspSlotDayDspSlotCodeAndTInputTime(dspSlotCode, inputTimeResult);
            if (dspSlotDayDOS == null) { // 濡傛灉鏌ヨ涓嶅埌灏辫烦杩?
                continue;
            }
            // 2 dspSlotDayDO 閲岄潰鏄鍏ユ椂闂寸殑棰勭畻鍜屽獟浣撴暟鎹紝
            //  绠楀嚭灞曠幇娆℃暟
            Long showCount  =  0L;
            for (DspSlotDayDO dspSlotDayDOVo : dspSlotDayDOS) {
                showCount += dspSlotDayDOVo.getShowPv();
            }
            // 绠楀嚭灞曠幇姣斾緥
            for (DspSlotDayDO dspSlotDayDO : dspSlotDayDOS) {
                DspSlotDayInputRespVo dspSlotDayInputRespVo1 = dspSlotDayMapper.selectDspSlotDayAllSpend(dspSlotCode, dspSlotDayDO.getDspSlotId(), dspSlotDayDO.getSspSlotId(), inputTimeResult);
                double pror = showCount == 0 ? 0D : (double) dspSlotDayDO.getShowPv() / showCount;
                SspSlotInfoDO sspSlotInfoDO = sspSlotInfoMapper.selectSspSlotId(dspSlotDayInputRespVo1.getSspSlotId()); // 获取分成比例
                int proportion = 0;
                if  (sspSlotInfoDO.getSspPayType() == 1 && sspSlotInfoDO.getSspDealRatio() != null) {
                    proportion = sspSlotInfoDO.getSspDealRatio();
                } else {
                    proportion = 80;
                }

                BigDecimal profit = Optional.ofNullable(inputExecTemplateVO.getProfit())
                        .orElse(BigDecimal.ZERO)
                        .multiply(BigDecimal.valueOf(pror));
                BigDecimal dspSpend = BigDecimal.ZERO;
                BigDecimal dspIncome = BigDecimal.ZERO;
                if (profit.compareTo(BigDecimal.ZERO) > 0) {
                    dspSpend = profit.multiply(BigDecimal.valueOf(0.8));
                    dspIncome = profit.subtract(dspSpend);
                }
                dspSlotDayInputRespVo1.setSpend(dspSpend);
                dspSlotDayInputRespVo1.setIncome(dspIncome);
                dspSlotDayInputRespVo1.setProfit(profit);
                dspSlotDayInputRespVo1.setProportion(proportion);

                dspSlotInfoInputRespVo.add(dspSlotDayInputRespVo1);
            }
        }
         return dspSlotInfoInputRespVo;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateInputIncomeData(List<InputIncomeReqVO> inputIncomeReqVOList) {
        // 收益为 0 的行在下方被跳过；若提交列表本身为空（全部为 0 或未提交），视为无需处理，直接成功返回
        if (inputIncomeReqVOList == null || inputIncomeReqVOList.isEmpty()) {
            return "收益保存成功";
        }
        Long inputIncomeId = inputIncomeReqVOList.get(0).getId();
        InputExecDO inputExecDO = inputExecMapper.selectInputExecId(inputIncomeId);
        if (inputExecDO == null) {
            throw ServiceExceptionUtil.exception0(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode(),
                    "收益保存失败：导入批次不存在（id=" + inputIncomeId + "）");
        }

        Long inputSize = 0L; // 导入条数

        // Excel 中允许存在收益为 0 的行：这些行跳过、不参与更新，避免因匹配不到数据导致整体保存失败
        List<InputIncomeReqVO> rowsToUpdate = new ArrayList<>();
        for (InputIncomeReqVO inputIncomeReqVO : inputIncomeReqVOList) {
            if (inputIncomeReqVO.getProfit() != null && inputIncomeReqVO.getProfit().signum() != 0) {
                rowsToUpdate.add(inputIncomeReqVO);
            }
        }
        for (InputIncomeReqVO inputIncomeReqVO : rowsToUpdate) {
            int i = dspSlotDayMapper.updateDspSlotDaySpendAndIncome(inputIncomeReqVO);
            if (i <= 0) {
                throw ServiceExceptionUtil.exception0(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode(),
                        "收益保存失败：预算广告位日报表未匹配到数据行（" + inputIncomeReqVO.getDspSlotCode()
                                + "，日期 " + inputIncomeReqVO.getDate() + "）");
            }
            int i1 = sspSlotDayMapper.updateSspSlotDaySpendAndIncome(inputIncomeReqVO);
            if (i1 <= 0) {
                throw ServiceExceptionUtil.exception0(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode(),
                        "收益保存失败：媒体广告位日报表未匹配到数据行（" + inputIncomeReqVO.getDspSlotCode()
                                + "，日期 " + inputIncomeReqVO.getDate() + "）");
            }
            inputSize++;
        }
        inputExecDO.setId(inputIncomeId);
        inputExecDO.setTables(inputSize);
        // 更新收益汇总后同步记录最后修改时间。
        inputExecDO.setUpdateTime(LocalDateTime.now());
        inputExecMapper.updateInputExec(inputExecDO);
        return "收益保存成功";
    }

}
