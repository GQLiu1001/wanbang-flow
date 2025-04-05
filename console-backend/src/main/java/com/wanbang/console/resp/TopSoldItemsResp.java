package com.wanbang.console.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TopSoldItemsResp {
    @JsonProperty("model_number")
    private String modelNumber;  // 产品型号
    //adjusted_quantity 和 total_quantity 在创建的时候一起创建 后续修改adjusted_quantity
    @JsonProperty("sales")
    private Integer adjustedQuantity; // 卖出的数量



}