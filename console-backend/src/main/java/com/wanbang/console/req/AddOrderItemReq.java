package com.wanbang.console.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddOrderItemReq {
    @JsonProperty("model_number")
    private String modelNumber;
    @JsonProperty("item_id")
    private Long itemId;
    private Integer quantity;
    private BigDecimal subtotal;
    @JsonProperty("price_per_piece")
    private BigDecimal pricePerPiece;
}
