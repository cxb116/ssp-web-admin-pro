package cn.iocoder.yudao.module.data.service.inputexec;

import cn.hutool.core.collection.CollUtil;
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




//        // 鎶ヨ〃鏁寸悊闆嗗悎瀵硅薄
//
//        // 1 灏嗗鍏ョ殑鏁版嵁锛屾寜鐓spSlotCode 鍒嗘垚鍑犲垎
//        List<ListInputExecArr> listInputExecArr = new ArrayList<>();
//        Map<String, List<InputExecTemplateVO>> groupMap = new LinkedHashMap<>();
//        for (InputExecTemplateVO vo : list) {
//            String key = vo.getDspSlotCode();
//            groupMap.computeIfAbsent(key, k -> new ArrayList<>()).add(vo);
//        }
//        for (Map.Entry<String, List<InputExecTemplateVO>> entry : groupMap.entrySet()) {
//            ListInputExecArr arr = new ListInputExecArr();
//            arr.setDspSlotCode(entry.getKey());
//            if (entry.getValue().get(0).getSpend() == 0) {
//             continue;
//            }
//            arr.setSpend(entry.getValue().get(0).getSpend() * 100);
//            arr.setListInputExecTemplateVO(entry.getValue());
//            listInputExecArr.add(arr);
//        }
//        // 閬嶅巻鏀堕泦鐨勯泦鍚?//
//       for (ListInputExecArr arr : listInputExecArr) {
//           String dspSlotCode = arr.getDspSlotCode();
//           if (arr.getSpend() == 0) {
//               continue;  // 鎺掗櫎鏀剁泭鏄?鐨勬暟鎹?//           }
//           // 閬嶅巻濯掍綋鏁版嵁
//           // 1 鑾峰彇鎵€鏈夌殑鏇濆厜娆℃暟
//           // 2 绠楀嚭姣忎釜濯掍綋鐨勬洕鍏夊崰姣?//           // 3 灏嗘敹鐩婂垎鍑?//           // 4 鍏ュ簱
//           Long sumShow = 0L;
//           for (InputExecTemplateVO listInputExec : arr.getListInputExecTemplateVO()) {
//
//               Long sspSlotId = listInputExec.getSspSlotId();
//               String inputTime = listInputExec.getInputTime();
//               LocalDate localDate = LocalDate.parse(inputTime);
//               String inputTimeResult = localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//
//               SspSlotDayDO sspSlotDayDO = sspSlotDayMapper.selectSspSlotId(sspSlotId,dspSlotCode, inputTimeResult);
//               System.out.println(sspSlotDayDO);
//               listInputExec.setShow(sspSlotDayDO.getShowPv());
//               sumShow += sspSlotDayDO.getShowPv();
//           }
//            System.out.println("== sumShow = " + sumShow);
//           for (InputExecTemplateVO listInputExec : arr.getListInputExecTemplateVO()) {
//              if (listInputExec.getShow() == 0) {
//                  listInputExec.setPercentage(0.0);
//                  continue;
//              }
//              double Percentage = (double) listInputExec.getShow() / sumShow * 100;
//              listInputExec.setPercentage(Percentage);
//           }
//
//           for (InputExecTemplateVO listInputExec : arr.getListInputExecTemplateVO()) {
//
//               Long sspSlotId = listInputExec.getSspSlotId();
//               String inputTime = listInputExec.getInputTime();
//               if (listInputExec.getShow() == 0 || listInputExec.getPercentage()== 0) {
//                   continue;
//               }
//               // 杩欎釜鏄垜鏂圭殑鏀剁泭 =  閲戦 X 鏇濆厜姣斾緥 / 100
//               Long spend = (long) (arr.getSpend() * listInputExec.getPercentage() / 100);
//               if (spend == 0) {
//                   continue;
//               }
//               Long income = (long) (spend * listInputExec.getSettleRate() / 100);
//               SspSlotDayDO sspSlotDayDO = new SspSlotDayDO();
//               sspSlotDayDO.setSspSlotId(sspSlotId);
//               sspSlotDayDO.setDspSlotCode(dspSlotCode);
//               // inputTime 鏍煎紡 "2026-10-10" 鈫?杞负 20261010
//               String dateStr = listInputExec.getInputTime().replace("-", "");
//               sspSlotDayDO.setDate(Long.valueOf(dateStr));
//               sspSlotDayDO.setIncome(income);
//               sspSlotDayDO.setSpend(spend);
//               sspSlotDayMapper.updateSspSlotDay(sspSlotDayDO);
//               // 淇敼dspSlotDayMapper
//               DspSlotDayDO dspSlotDayDO = new DspSlotDayDO();
//               dspSlotDayDO.setSspSlotId(sspSlotId);
//               dspSlotDayDO.setDspSlotCode(dspSlotCode);
//               dspSlotDayDO.setDate(Long.valueOf(dateStr));
//               dspSlotDayDO.setIncome(income);
//               dspSlotDayDO.setSpend(spend);
//               dspSlotDayMapper.updateSsspSlotDay(dspSlotDayDO);
//           }
////           InputExecDO inputExecDO = new InputExecDO();
////           inputExecDO.setId(arr.getListInputExecTemplateVO().get(0).getId());
////           // inputTime 鏍煎紡 "2026-10-10" 鈫?杞负 20261010
////           inputExecDO.setInputTime(arr.getListInputExecTemplateVO().get(0).getInputTime());
////           inputExecDO.setUpdateTime(LocalDateTime.now());
////           inputExecMapper.updateInputExec(inputExecDO);
//       }
//        return "瀵煎叆鎴愬姛";

        // 鐒跺悗閫愪釜鍘昏绠楀鍏ユ敹鐩?

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
//        return "瀵煎叆瀹屾垚锛屾垚鍔?" + successCount + " 鏉★紝澶辫触 " + failCount + " 鏉?;
    }

    @Override
    public String updateInputIncomeData(List<InputIncomeReqVO> inputIncomeReqVOList) {

//        inputExecMapper.selectDspSlotCodeDate()
        Long inputIncomeId = 0L;
        InputExecDO inputExecDO = null;
        if (inputIncomeReqVOList.size() > 0) {
            InputIncomeReqVO inputIncomeReqVO1 = inputIncomeReqVOList.get(0);
            inputIncomeId = inputIncomeReqVO1.getId();
            inputExecDO = inputExecMapper.selectInputExecId(inputIncomeId);
        }

        Long inputSize = 0L; // 导入条数

        if (inputIncomeReqVOList == null || inputIncomeReqVOList.isEmpty()) {
            return "收益保存失败";
        }
        for (InputIncomeReqVO inputIncomeReqVO : inputIncomeReqVOList) {
            int i = dspSlotDayMapper.updateDspSlotDaySpendAndIncome(inputIncomeReqVO);
            if (i > 0) {
                int i1 = sspSlotDayMapper.updateSspSlotDaySpendAndIncome(inputIncomeReqVO);
                inputSize++;
                if (i1 == 0) {
                    return "收益保存失败";
                }
            } else {
                return "收益保存失败";
            }
        }
        inputExecDO.setId(inputIncomeId);
        inputExecDO.setTables(inputSize);
        // 更新收益汇总后同步记录最后修改时间。
        inputExecDO.setUpdateTime(LocalDateTime.now());
        inputExecMapper.updateInputExec(inputExecDO);
        return "收益保存成功";
    }

}
