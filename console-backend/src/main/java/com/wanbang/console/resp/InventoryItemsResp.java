package com.wanbang.console.resp;

import com.wanbang.console.common.InventoryItem;
import lombok.Data;

import java.util.List;
//返回产品列表
@Data
public class InventoryItemsResp {
    private Long total;
    private Long page;
    private Long size;

    private List<InventoryItem> items;
}
