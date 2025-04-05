package com.wanbang.console.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
//创建调库记录
@Data
public class PostTransferReq {
    //operate_type 请求参数
    @JsonProperty("inventory_item_id")
    private Long inventoryItemId;
    @JsonProperty("operator_id")
    private Long operatorId;
    @JsonProperty("source_warehouse")
    private Integer sourceWarehouse;
    @JsonProperty("target_warehouse")
    private Integer targetWarehouse;
    private String remark;

}
