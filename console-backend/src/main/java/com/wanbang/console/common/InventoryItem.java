package com.wanbang.console.common;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 瓷砖库存表
 * @TableName inventory_item
 */
@Data
public class InventoryItem {
    /**
     * 库存ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 产品型号
     */
    @JsonProperty("model_number")
    private String modelNumber;

    /**
     * 制造厂商
     */

    private String manufacturer;

    /**
     * 规格（如：600x600mm）
     */
    private String specification;

    /**
     * 表面处理（1=抛光 2=哑光 3=釉面 4=通体大理石 5=微晶石 6=岩板）
     */
    private Integer surface;

    /**
     * 分类（1=墙砖 2=地砖）
     */
    private Integer category;

    /**
     * 仓库编码
     */
    @JsonProperty("warehouse_num")
    private Integer warehouseNum;

    /**
     * 总片数
     */
    @JsonProperty("total_pieces")
    private Integer totalPieces;

    /**
     * 单片价格（单位：元）
     */
    @JsonProperty("price_per_piece")
    private BigDecimal pricePerPiece;

    /**
     * 每箱片数
     */
    @JsonProperty("pieces_per_box")
    private Integer piecesPerBox;

    /**
     * 备注
     */
    private String remark;

    /**
     * 
     */
    @JsonProperty("create_time")
    private Date createTime;

    /**
     * 
     */
    @JsonProperty("update_time")
    private Date updateTime;

}