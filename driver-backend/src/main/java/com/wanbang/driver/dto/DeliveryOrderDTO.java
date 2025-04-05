package com.wanbang.driver.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class DeliveryOrderDTO {
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
     * 货物重量(吨)
     */
    private BigDecimal goodsWeight;

    /**
     * 创建时间
     */
    private Date createTime;
}
