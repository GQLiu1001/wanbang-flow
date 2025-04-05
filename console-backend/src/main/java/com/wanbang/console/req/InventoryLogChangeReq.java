package com.wanbang.console.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InventoryLogChangeReq {
    private Long id;
    @JsonProperty("inventory_item_id")
    private Long inventoryItemId;
    @JsonProperty("operator_id")
    private Long operatorId;
    @JsonProperty("operation_type")
    private Integer operationType;
    @JsonProperty("source_warehouse")
    private Integer sourceWarehouse;
    @JsonProperty("target_warehouse")
    private Integer targetWarehouse;
    @JsonProperty("quantity_change")
    private Integer quantityChange;
    private String remark;
}
