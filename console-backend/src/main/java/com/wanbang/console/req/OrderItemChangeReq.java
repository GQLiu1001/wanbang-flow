package com.wanbang.console.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemChangeReq {
    private Integer quantity;
    private BigDecimal subtotal;
    @JsonProperty("price_per_piece")
    private BigDecimal pricePerPiece;

}
