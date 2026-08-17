package cn.iocoder.yudao.module.data.controller.admin.inputexec.vo;

import lombok.Data;

import java.util.List;

@Data
public class ListInputExecArr {
    private String dspSlotCode;
    private Long   spend;

    private List<InputExecTemplateVO> ListInputExecTemplateVO;

}
