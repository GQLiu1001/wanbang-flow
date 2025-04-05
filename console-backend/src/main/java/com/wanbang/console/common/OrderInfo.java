package com.wanbang.console.common;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单主表
 * @TableName order_info
 */
@Data
public class OrderInfo {
    /**
     * 订单ID（主键）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号（业务编号，雪花算法生成）
     */
    @JsonProperty("order_no")
    private String orderNo;

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
     * 原始订单总金额
     */
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    /**
     * 调整后的总金额（多退少补后）
     */
    @JsonProperty("adjusted_amount")
    private BigDecimal adjustedAmount;

    /**
     * 售后状态（1=新建 2=已解决）
     */
    @JsonProperty("aftersale_status")
    private Integer aftersaleStatus;
    /**
     * 派单状态
     */
    @JsonProperty("dispatch_status")
    private Integer dispatchStatus;

    /**
     * 订单创建时间
     */
    @TableField("order_create_time")
    private Date orderCreateTime;

    /**
     * 订单更新时间
     */
    @TableField("order_update_time")
    private Date orderUpdateTime;

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
        OrderInfo other = (OrderInfo) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOrderNo() == null ? other.getOrderNo() == null : this.getOrderNo().equals(other.getOrderNo()))
            && (this.getCustomerPhone() == null ? other.getCustomerPhone() == null : this.getCustomerPhone().equals(other.getCustomerPhone()))
            && (this.getOperatorId() == null ? other.getOperatorId() == null : this.getOperatorId().equals(other.getOperatorId()))
            && (this.getOrderRemark() == null ? other.getOrderRemark() == null : this.getOrderRemark().equals(other.getOrderRemark()))
            && (this.getTotalAmount() == null ? other.getTotalAmount() == null : this.getTotalAmount().equals(other.getTotalAmount()))
            && (this.getAdjustedAmount() == null ? other.getAdjustedAmount() == null : this.getAdjustedAmount().equals(other.getAdjustedAmount()))
            && (this.getAftersaleStatus() == null ? other.getAftersaleStatus() == null : this.getAftersaleStatus().equals(other.getAftersaleStatus()))
            && (this.getOrderCreateTime() == null ? other.getOrderCreateTime() == null : this.getOrderCreateTime().equals(other.getOrderCreateTime()))
            && (this.getOrderUpdateTime() == null ? other.getOrderUpdateTime() == null : this.getOrderUpdateTime().equals(other.getOrderUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOrderNo() == null) ? 0 : getOrderNo().hashCode());
        result = prime * result + ((getCustomerPhone() == null) ? 0 : getCustomerPhone().hashCode());
        result = prime * result + ((getOperatorId() == null) ? 0 : getOperatorId().hashCode());
        result = prime * result + ((getOrderRemark() == null) ? 0 : getOrderRemark().hashCode());
        result = prime * result + ((getTotalAmount() == null) ? 0 : getTotalAmount().hashCode());
        result = prime * result + ((getAdjustedAmount() == null) ? 0 : getAdjustedAmount().hashCode());
        result = prime * result + ((getAftersaleStatus() == null) ? 0 : getAftersaleStatus().hashCode());
        result = prime * result + ((getOrderCreateTime() == null) ? 0 : getOrderCreateTime().hashCode());
        result = prime * result + ((getOrderUpdateTime() == null) ? 0 : getOrderUpdateTime().hashCode());
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
        sb.append(", customerPhone=").append(customerPhone);
        sb.append(", operatorId=").append(operatorId);
        sb.append(", orderRemark=").append(orderRemark);
        sb.append(", totalAmount=").append(totalAmount);
        sb.append(", adjustedAmount=").append(adjustedAmount);
        sb.append(", aftersaleStatus=").append(aftersaleStatus);
        sb.append(", orderCreateTime=").append(orderCreateTime);
        sb.append(", orderUpdateTime=").append(orderUpdateTime);
        sb.append("]");
        return sb.toString();
    }
}