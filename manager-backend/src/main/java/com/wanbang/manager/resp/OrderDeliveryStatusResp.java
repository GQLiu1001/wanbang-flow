package com.wanbang.manager.resp;

import lombok.Data;

@Data
public class OrderDeliveryStatusResp {

    private Integer deliveryStatus;
    private Long driverId;
    private String driverName;
    private String driverPhone;



}
