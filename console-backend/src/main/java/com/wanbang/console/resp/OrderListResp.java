package com.wanbang.console.resp;

import com.wanbang.console.dto.OrderInfoDTO;
import lombok.Data;

import java.util.List;

@Data
public class OrderListResp {
    private Long total;
    private Long page;
    private Long size;
    private List<OrderInfoDTO> items;

}
