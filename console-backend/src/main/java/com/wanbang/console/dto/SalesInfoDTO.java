package com.wanbang.console.dto;

import lombok.Data;

@Data
public class SalesInfoDTO {

    //总共销售数量
    private Integer adjustedQuantity;
    //总共钱数
    private Integer adjustedAmount;
}
