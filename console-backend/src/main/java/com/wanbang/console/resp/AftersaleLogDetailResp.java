package com.wanbang.console.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AftersaleLogDetailResp {
    private Long id;

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("aftersale_type")
    private Integer aftersaleType;

    @JsonProperty("aftersale_status")
    private Integer aftersaleStatus;

    @JsonProperty("resolution_result")
    private String resolutionResult;

    @JsonProperty("aftersale_operator")
    private Long aftersaleOperator;

    @JsonProperty("create_time")
    private Date createTime;

    @JsonProperty("order_item_id")
    private Long orderItemId;

    @JsonProperty("quantity_change")
    private Integer quantityChange;

    @JsonProperty("amount_change")
    private BigDecimal amountChange;

}