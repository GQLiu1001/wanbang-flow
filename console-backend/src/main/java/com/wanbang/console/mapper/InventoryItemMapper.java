package com.wanbang.console.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wanbang.console.common.InventoryItem;

/**
* @author 11965
* @description 针对表【inventory_item(瓷砖库存表)】的数据库操作Mapper
* @createDate 2025-02-24 15:24:52
* @Entity com.wanbang.common.InventoryItem
*/
public interface InventoryItemMapper extends BaseMapper<InventoryItem> {

    IPage<InventoryItem> selectItemsList(IPage<InventoryItem> pageParam, Integer category, Integer surface);

    Long findInventoryItemId(String modelNumber);

    Integer updateTotalPieces(String modelNumber, Integer totalPieces);

    Integer transfer(Long inventoryItemId, Integer targetWarehouse);

    Integer itemReversal(Integer operationType, Long inventoryItemId, Integer sourceWarehouse, Integer quantityChange);

    Integer outbound(Long itemId, Integer quantity);
}




