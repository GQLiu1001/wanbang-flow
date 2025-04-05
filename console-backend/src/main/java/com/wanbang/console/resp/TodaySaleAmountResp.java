package com.wanbang.console.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
//今天卖出多少钱
@Data
public class TodaySaleAmountResp {
    @JsonProperty("today_sale_amount")
    private Integer todaySaleAmount;
}
