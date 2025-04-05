package com.wanbang.manager.resp;



import com.wanbang.manager.common.DeliveryOrder;
import lombok.Data;

import java.util.List;

@Data
public class DeliveryOrderResp {
    private Long total;
    private List<DeliveryOrder> records;
}
