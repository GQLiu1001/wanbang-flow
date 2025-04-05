package com.wanbang.console.common;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单售后日志表
 * @TableName order_aftersale_log
 */
@Data
public class OrderAftersaleLog {
    /**
     * 唯一标识
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    @JsonProperty("order_id")
    private Long orderId;

    /**
     * 订单项ID
     */
    @JsonProperty("order_item_id")
    private Long orderItemId;

    /**
     * 售后类型（1=买多退货退款 2=买少补货补款）
     */
    @JsonProperty("aftersale_type")
    private Integer aftersaleType;

    /**
     * 售后状态（1=新建 2=已解决）
     */
    @JsonProperty("aftersale_status")
    private Integer aftersaleStatus;

    /**
     * 数量变化（负数表示退货，正数表示补货）
     */
    @JsonProperty("quantity_change")
    private Integer quantityChange;

    /**
     * 金额变化（负数表示退款，正数表示补款）
     */
    @JsonProperty("amount_change")
    private BigDecimal amountChange;

    /**
     * 处理结果说明
     */
    @JsonProperty("resolution_result")
    private String resolutionResult;

    /**
     * 售后处理人ID
     */
    @JsonProperty("aftersale_operator")
    private Long aftersaleOperator;

    /**
     * 创建时间
     */
    @JsonProperty("creat_time")
    private Date createTime;

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
        OrderAftersaleLog other = (OrderAftersaleLog) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOrderId() == null ? other.getOrderId() == null : this.getOrderId().equals(other.getOrderId()))
            && (this.getOrderItemId() == null ? other.getOrderItemId() == null : this.getOrderItemId().equals(other.getOrderItemId()))
            && (this.getAftersaleType() == null ? other.getAftersaleType() == null : this.getAftersaleType().equals(other.getAftersaleType()))
            && (this.getAftersaleStatus() == null ? other.getAftersaleStatus() == null : this.getAftersaleStatus().equals(other.getAftersaleStatus()))
            && (this.getQuantityChange() == null ? other.getQuantityChange() == null : this.getQuantityChange().equals(other.getQuantityChange()))
            && (this.getAmountChange() == null ? other.getAmountChange() == null : this.getAmountChange().equals(other.getAmountChange()))
            && (this.getResolutionResult() == null ? other.getResolutionResult() == null : this.getResolutionResult().equals(other.getResolutionResult()))
            && (this.getAftersaleOperator() == null ? other.getAftersaleOperator() == null : this.getAftersaleOperator().equals(other.getAftersaleOperator()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOrderId() == null) ? 0 : getOrderId().hashCode());
        result = prime * result + ((getOrderItemId() == null) ? 0 : getOrderItemId().hashCode());
        result = prime * result + ((getAftersaleType() == null) ? 0 : getAftersaleType().hashCode());
        result = prime * result + ((getAftersaleStatus() == null) ? 0 : getAftersaleStatus().hashCode());
        result = prime * result + ((getQuantityChange() == null) ? 0 : getQuantityChange().hashCode());
        result = prime * result + ((getAmountChange() == null) ? 0 : getAmountChange().hashCode());
        result = prime * result + ((getResolutionResult() == null) ? 0 : getResolutionResult().hashCode());
        result = prime * result + ((getAftersaleOperator() == null) ? 0 : getAftersaleOperator().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
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
        sb.append(", orderItemId=").append(orderItemId);
        sb.append(", aftersaleType=").append(aftersaleType);
        sb.append(", aftersaleStatus=").append(aftersaleStatus);
        sb.append(", quantityChange=").append(quantityChange);
        sb.append(", amountChange=").append(amountChange);
        sb.append(", resolutionResult=").append(resolutionResult);
        sb.append(", aftersaleOperator=").append(aftersaleOperator);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}