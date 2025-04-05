package com.wanbang.driver.common;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 配送订单表
 * @TableName delivery_order
 */
@TableName(value ="delivery_order")
@Data
public class DeliveryOrder implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 司机ID
     */
    private Long driverId;

    /**
     * 司机手机号
     */
    private String customerPhone;

    /**
     * 派送地址
     */
    private String deliveryAddress;

    /**
     * 配送状态(1=待派送,2=待接单,3=配送中,4=已完成,5=已取消)
     */
    private Integer deliveryStatus;

    /**
     * 配送费用
     */
    private BigDecimal deliveryFee;

    /**
     * 配送备注
     */
    private String deliveryNote;

    /**
     * 货物重量(吨)
     */
    private BigDecimal goodsWeight;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
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
        DeliveryOrder other = (DeliveryOrder) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOrderNo() == null ? other.getOrderNo() == null : this.getOrderNo().equals(other.getOrderNo()))
            && (this.getDriverId() == null ? other.getDriverId() == null : this.getDriverId().equals(other.getDriverId()))
            && (this.getCustomerPhone() == null ? other.getCustomerPhone() == null : this.getCustomerPhone().equals(other.getCustomerPhone()))
            && (this.getDeliveryAddress() == null ? other.getDeliveryAddress() == null : this.getDeliveryAddress().equals(other.getDeliveryAddress()))
            && (this.getDeliveryStatus() == null ? other.getDeliveryStatus() == null : this.getDeliveryStatus().equals(other.getDeliveryStatus()))
            && (this.getDeliveryFee() == null ? other.getDeliveryFee() == null : this.getDeliveryFee().equals(other.getDeliveryFee()))
            && (this.getDeliveryNote() == null ? other.getDeliveryNote() == null : this.getDeliveryNote().equals(other.getDeliveryNote()))
            && (this.getGoodsWeight() == null ? other.getGoodsWeight() == null : this.getGoodsWeight().equals(other.getGoodsWeight()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOrderNo() == null) ? 0 : getOrderNo().hashCode());
        result = prime * result + ((getDriverId() == null) ? 0 : getDriverId().hashCode());
        result = prime * result + ((getCustomerPhone() == null) ? 0 : getCustomerPhone().hashCode());
        result = prime * result + ((getDeliveryAddress() == null) ? 0 : getDeliveryAddress().hashCode());
        result = prime * result + ((getDeliveryStatus() == null) ? 0 : getDeliveryStatus().hashCode());
        result = prime * result + ((getDeliveryFee() == null) ? 0 : getDeliveryFee().hashCode());
        result = prime * result + ((getDeliveryNote() == null) ? 0 : getDeliveryNote().hashCode());
        result = prime * result + ((getGoodsWeight() == null) ? 0 : getGoodsWeight().hashCode());
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
        sb.append(", orderNo=").append(orderNo);
        sb.append(", driverId=").append(driverId);
        sb.append(", customerPhone=").append(customerPhone);
        sb.append(", deliveryAddress=").append(deliveryAddress);
        sb.append(", deliveryStatus=").append(deliveryStatus);
        sb.append(", deliveryFee=").append(deliveryFee);
        sb.append(", deliveryNote=").append(deliveryNote);
        sb.append(", goodsWeight=").append(goodsWeight);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}