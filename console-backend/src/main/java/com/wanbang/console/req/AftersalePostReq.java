package com.wanbang.console.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wanbang.console.common.ItemAftersaleChange;
import lombok.Data;

import java.util.List;

@Data
public class AftersalePostReq {
    @JsonProperty("order_id")
    private Long orderId;
    @JsonProperty("aftersale_type")
    private Integer aftersaleType;
    @JsonProperty("aftersale_status")
    private Integer aftersaleStatus;
    @JsonProperty("resolution_result")
    private String resolutionResult;
    @JsonProperty("aftersale_operator")
    private Integer aftersaleOperator;
    private List<ItemAftersaleChange> items;
}
