package com.wanbang.console.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemAftersaleChange {
    @JsonProperty("order_item_id")
    private Long orderItemId;

    @JsonProperty("quantity_change")
    private Integer quantityChange;

    @JsonProperty("amount_change")
    private BigDecimal amountChange;
}
