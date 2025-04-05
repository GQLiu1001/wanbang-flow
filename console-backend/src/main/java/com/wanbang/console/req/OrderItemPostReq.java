// 文件: com/wanbang/req/OrderItemPostReq.java
package com.wanbang.console.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单项请求
 */
@Data
public class OrderItemPostReq {
    /**
     * 库存商品ID
     */
    @JsonProperty("item_id")
    private Long itemId;

    /**
     * 产品型号
     */
    @JsonProperty("model_number")
    private String modelNumber;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 单价
     */
    @JsonProperty("price_per_piece")
    private BigDecimal pricePerPiece;

    /**
     * 小计金额
     */
    private BigDecimal subtotal;
    @JsonProperty("source_warehouse")
    private Integer sourceWarehouse;
}