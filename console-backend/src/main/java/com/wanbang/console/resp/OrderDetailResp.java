package com.wanbang.console.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.wanbang.console.common.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
@JsonPropertyOrder({
        "id", "order_no", "customer_phone", "operator_id", "order_remark",
        "total_amount", "adjusted_amount", "aftersale_status",
        "order_create_time", "order_update_time", "items"
})
@Data
public class OrderDetailResp {
    //OrderInfo
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
    @JsonProperty("order_create_time")
    private Date orderCreateTime;
    @JsonProperty("order_update_time")
    private Date orderUpdateTime;
    //List OrderItem
    private List<OrderItem> items;
}
