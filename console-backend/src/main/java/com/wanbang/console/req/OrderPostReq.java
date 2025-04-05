// 文件: com/wanbang/req/OrderPostReq.java
package com.wanbang.console.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单提交请求
 */
@Data
public class OrderPostReq {
    /**
     * 客户手机号
     */
    @JsonProperty("customer_phone")
    private String customerPhone;

    /**
     * 操作人ID
     */
    @JsonProperty("operator_id")
    private Long operatorId;

    /**
     * 订单备注
     */
    @JsonProperty("order_remark")
    private String orderRemark;

    /**
     * 订单总金额
     */
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    /**
     * 订单项列表
     */
    private List<OrderItemPostReq> items;
}