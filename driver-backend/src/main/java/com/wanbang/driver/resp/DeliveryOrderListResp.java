package com.wanbang.driver.resp;



import com.wanbang.driver.dto.DeliveryOrderDTO;
import lombok.Data;

import java.util.List;
@Data
public class DeliveryOrderListResp {
    private Long total;
    private List<DeliveryOrderDTO> records;
}
