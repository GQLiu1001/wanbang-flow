package com.wanbang.console.resp;

import com.wanbang.console.common.InventoryLog;
import lombok.Data;

import java.util.List;
//返回调库 入库 出库记录resp
@Data
public class InventoryLogResp {
    private Long total;
    private Long page;
    private Long size;
    private List<InventoryLog> items;
}
