package com.wanbang.console.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

//产品修改
@Data
public class InventoryItemsChangeReq {
    @JsonProperty("model_number")
    private String modelNumber;
    private String manufacturer;
    private String specification;
    private Integer surface;
    private Integer category;
    @JsonProperty("warehouse_num")
    private Integer warehouseNum;
    @JsonProperty("total_pieces")
    private Integer totalPieces;
    @JsonProperty("price_per_piece")
    private BigDecimal pricePerPiece;
    @JsonProperty("pieces_per_box")
    private Integer piecesPerBox;
    private String remark;
}
