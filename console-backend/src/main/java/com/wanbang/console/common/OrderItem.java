package com.wanbang.console.common;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单项表
 * @TableName order_item
 */
@Data
public class OrderItem {
    /**
     * 订单项ID（主键）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID（外键关联order_info表）
     */
    @JsonProperty("order_id")
    private Long orderId;

    /**
     * 库存商品ID（外键关联inventory_item表）
     */
    @JsonProperty("item_id")
    private Long itemId;

    /**
     * 产品型号
     */
    @JsonProperty("model_number")
    private String modelNumber;

    /**
     * 原始购买数量
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

    /**
     * 调整后的数量
     */
    @JsonProperty("adjusted_quantity")
    private Integer adjustedQuantity;

    /**
     * 创建时间
     */
    @JsonProperty("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonProperty("update_time")
    private Date updateTime;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        OrderItem other = (OrderItem) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOrderId() == null ? other.getOrderId() == null : this.getOrderId().equals(other.getOrderId()))
            && (this.getItemId() == null ? other.getItemId() == null : this.getItemId().equals(other.getItemId()))
            && (this.getModelNumber() == null ? other.getModelNumber() == null : this.getModelNumber().equals(other.getModelNumber()))
            && (this.getQuantity() == null ? other.getQuantity() == null : this.getQuantity().equals(other.getQuantity()))
            && (this.getPricePerPiece() == null ? other.getPricePerPiece() == null : this.getPricePerPiece().equals(other.getPricePerPiece()))
            && (this.getSubtotal() == null ? other.getSubtotal() == null : this.getSubtotal().equals(other.getSubtotal()))
            && (this.getAdjustedQuantity() == null ? other.getAdjustedQuantity() == null : this.getAdjustedQuantity().equals(other.getAdjustedQuantity()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOrderId() == null) ? 0 : getOrderId().hashCode());
        result = prime * result + ((getItemId() == null) ? 0 : getItemId().hashCode());
        result = prime * result + ((getModelNumber() == null) ? 0 : getModelNumber().hashCode());
        result = prime * result + ((getQuantity() == null) ? 0 : getQuantity().hashCode());
        result = prime * result + ((getPricePerPiece() == null) ? 0 : getPricePerPiece().hashCode());
        result = prime * result + ((getSubtotal() == null) ? 0 : getSubtotal().hashCode());
        result = prime * result + ((getAdjustedQuantity() == null) ? 0 : getAdjustedQuantity().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", orderId=").append(orderId);
        sb.append(", itemId=").append(itemId);
        sb.append(", modelNumber=").append(modelNumber);
        sb.append(", quantity=").append(quantity);
        sb.append(", pricePerPiece=").append(pricePerPiece);
        sb.append(", subtotal=").append(subtotal);
        sb.append(", adjustedQuantity=").append(adjustedQuantity);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}