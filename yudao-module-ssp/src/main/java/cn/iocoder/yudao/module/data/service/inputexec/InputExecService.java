package cn.iocoder.yudao.module.data.service.inputexec;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.data.controller.admin.inputexec.vo.*;
import cn.iocoder.yudao.module.data.dal.dataobject.inputexec.InputExecDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * DSP鏁版嵁瀵煎叆 Service 鎺ュ彛
 *
 * @author 鑺嬮亾婧愮爜
 */
public interface InputExecService {

    /**
     * 鍒涘缓DSP鏁版嵁瀵煎叆
     *
     * @param createReqVO 鍒涘缓淇℃伅
     * @return 缂栧彿
     */
    Long createInputExec(@Valid InputExecSaveReqVO createReqVO);

    /**
     * 鏇存柊DSP鏁版嵁瀵煎叆
     *
     * @param updateReqVO 鏇存柊淇℃伅
     */
    void updateInputExec(@Valid InputExecSaveReqVO updateReqVO);

    /**
     * 鍒犻櫎DSP鏁版嵁瀵煎叆
     *
     * @param id 缂栧彿
     */
    void deleteInputExec(Long id);

    /**
    * 鎵归噺鍒犻櫎DSP鏁版嵁瀵煎叆
    *
    * @param ids 缂栧彿
    */
    void deleteInputExecListByIds(List<Long> ids);

    /**
     * 鑾峰緱DSP鏁版嵁瀵煎叆
     *
     * @param id 缂栧彿
     * @return DSP鏁版嵁瀵煎叆
     */
    InputExecDO getInputExec(Long id);

    /**
     * 鑾峰緱DSP鏁版嵁瀵煎叆鍒嗛〉
     *
     * @param pageReqVO 鍒嗛〉鏌ヨ
     * @return DSP鏁版嵁瀵煎叆鍒嗛〉
     */
    PageResult<InputExecDO> getInputExecPage(InputExecPageReqVO pageReqVO);

    List<InputExecDO> getDownExcelInput(Long id,String inputTime);

    /**
     * 瀵煎叆DSP鏁版嵁瀵煎叆鍒楄〃
     *
     * @param list 瀵煎叆鏁版嵁鍒楄〃
     * @param companyId 鍏徃ID
     * @return 瀵煎叆缁撴灉
     */
    List<DspSlotDayInputRespVo> importExecList(List<InputExecTemplateVO> list, Long companyId);
    // 淇濆瓨鏀跺叆鏁版嵁
    String updateInputIncomeData(@Valid List<InputIncomeReqVO> inputIncomeReqVOList);
}

