package com.wanbang.console.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderInfoDTO {
    @JsonProperty("items_count")
    private Integer itemsCount;
    private Long id;
    @JsonProperty("order_no")
    private String orderNo;
    @JsonProperty("customer_phone")
    private String customerPhone;
    @JsonProperty("operator_id")
    private Long operatorId;
    @JsonProperty("order_remark")
    private String orderRemark;
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;
    @JsonProperty("adjusted_amount")
    private BigDecimal adjustedAmount;
    @JsonProperty("aftersale_status")
    private Integer aftersaleStatus;
    @JsonProperty("dispatch_status")
    private Integer dispatchStatus;
    @JsonProperty("order_create_time")
    private Date orderCreateTime;
    @JsonProperty("order_update_time")
    private Date orderUpdateTime;
}
