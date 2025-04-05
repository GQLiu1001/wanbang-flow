package com.wanbang.console.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wanbang.console.common.InventoryItem;
import com.wanbang.console.common.InventoryLog;
import com.wanbang.console.common.ItemAftersaleChange;
import com.wanbang.console.req.InventoryItemsChangeReq;
import com.wanbang.console.req.OrderItemPostReq;
import com.wanbang.console.req.PostInboundReq;
import com.wanbang.console.resp.ItemModelResp;

import java.util.List;

/**
* @author 11965
* @description 针对表【inventory_item(瓷砖库存表)】的数据库操作Service
* @createDate 2025-02-24 15:24:52
*/
public interface InventoryItemService extends IService<InventoryItem> {

    IPage<InventoryItem> getItems(Integer page, Integer size, Integer category, Integer surface);

    Integer changeItems(Integer id, InventoryItemsChangeReq req);

    Integer deleteById(Integer id);

    Integer postInboundItem(PostInboundReq postInboundReq);

    Integer transfer(Integer sourceWarehouse,Long inventoryItemId, Integer targetWarehouse);


    Integer itemReversal(InventoryLog req);

    Integer outbound(List<OrderItemPostReq> items);


    Integer aftersale(ItemAftersaleChange item);

    ItemModelResp getItemModel(String modelNumber);
}
