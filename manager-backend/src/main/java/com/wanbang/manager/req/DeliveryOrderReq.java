package com.wanbang.manager.req;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class DeliveryOrderReq implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private String orderNo;
    private String customerPhone;
    private String deliveryAddress;
    private BigDecimal deliveryFee;
    private BigDecimal goodsWeight;
}
