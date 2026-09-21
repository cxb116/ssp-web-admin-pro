package cn.iocoder.yudao.module.data.controller.admin.inputexec.vo;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class InputIncomeReqVO {
    private Long id;
    private String dspSlotCode;
    private Long   dspSlotId;
    private Long sspSlotId;
    private Long date;
    private BigDecimal profit;
    private BigDecimal spend;
    private BigDecimal income;

}
