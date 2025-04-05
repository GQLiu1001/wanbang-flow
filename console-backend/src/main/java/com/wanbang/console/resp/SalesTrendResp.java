package com.wanbang.console.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

// 销售趋势响应类
@Data
public class SalesTrendResp {
    private String dates;
    @JsonProperty("salesValues")
    private Integer adjustedQuantity;
    @JsonProperty("amounts")
    private Integer adjustedAmount;
}