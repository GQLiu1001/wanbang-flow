package com.wanbang.console.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OrderChangeReq {
    @JsonProperty("customer_phone")
    private String customerPhone;
    @JsonProperty("operator_id")
    private Long operatorId;
    @JsonProperty("order_remark")
    private String orderRemark;
}
