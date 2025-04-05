package com.wanbang.console.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
//总共卖了多少钱
@Data
public class TotalSaleAmountResp {
    @JsonProperty("total_sale_amount")
    private int totalSaleAmount;
}
