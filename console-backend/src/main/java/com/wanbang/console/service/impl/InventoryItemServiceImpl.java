package com.wanbang.console.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanbang.console.common.*;
import com.wanbang.console.mapper.InventoryItemMapper;
import com.wanbang.console.mapper.OrderItemMapper;
import com.wanbang.console.req.InventoryItemsChangeReq;
import com.wanbang.console.req.OrderItemPostReq;
import com.wanbang.console.req.PostInboundReq;
import com.wanbang.console.resp.ItemModelResp;
import com.wanbang.console.service.InventoryItemService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author 11965
* @description 针对表【inventory_item(瓷砖库存表)】的数据库操作Service实现
* @createDate 2025-02-24 15:24:52
*/
@Service
public class InventoryItemServiceImpl extends ServiceImpl<InventoryItemMapper, InventoryItem>
    implements InventoryItemService{
    @Resource
    private InventoryItemMapper inventoryItemMapper;
    @Resource
    private OrderItemMapper orderItemMapper;
    @Override
    public IPage<InventoryItem> getItems(Integer page, Integer size,Integer category,Integer surface) {
        IPage<InventoryItem> pageParam = new Page<>(page, size);
        IPage<InventoryItem> result = inventoryItemMapper.selectItemsList(pageParam,category,surface);
        System.out.println(result);
        return result;
    }

    @Override
    public Integer changeItems(Integer id, InventoryItemsChangeReq req) {
        InventoryItem item = new InventoryItem();
        BeanUtils.copyProperties(req,item);
        item.setUpdateTime(new Date());
        item.setId(Long.valueOf(id));
        System.out.println(item);
        LambdaUpdateWrapper<InventoryItem> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(InventoryItem::getId, item.getId());
        int update = inventoryItemMapper.update(item, updateWrapper);
        System.out.println(update);
        return update;
    }

    @Override
    public Integer deleteById(Integer id) {
        LambdaUpdateWrapper<InventoryItem> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(InventoryItem::getId, id);
        int update = inventoryItemMapper.delete(wrapper);
        System.out.println(update);
        return update;
    }

    @Override
    public Integer postInboundItem(PostInboundReq postInboundReq) {
        String modelNumber = postInboundReq.getModelNumber();
        LambdaUpdateWrapper<InventoryItem> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(InventoryItem::getModelNumber, modelNumber);
        boolean exists = inventoryItemMapper.exists(wrapper);
        if (exists) {
            Integer k = inventoryItemMapper.updateTotalPieces(modelNumber,postInboundReq.getTotalPieces());
            if (k > 0) {
                return k;
            }
            return 0;
        }else {
            InventoryItem item = new InventoryItem();
            BeanUtils.copyProperties(postInboundReq, item);
            item.setCreateTime(new Date());
            item.setUpdateTime(new Date());
            System.out.println(item);
            int update = inventoryItemMapper.insert(item);
            System.out.println("update:" + update);
            return update;
        }
    }

    @Override
    public Integer transfer(Integer sourceWarehouse,Long inventoryItemId, Integer targetWarehouse) {
        LambdaUpdateWrapper<InventoryItem> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(InventoryItem::getId, inventoryItemId);
        System.out.println("inventoryItemId = " + inventoryItemId);
        Integer i = inventoryItemMapper.transfer(inventoryItemId,targetWarehouse);
        System.out.println("i = " + i);
        return i;
    }

    @Override
    public Integer itemReversal(InventoryLog req) {
        //如果是调库 只需要改Item的仓库名称
        //如果是入库 只需要改Item的数量
        //如果是出库 只需要改Item的数量
        Long inventoryItemId = req.getInventoryItemId();
        Integer operationType = req.getOperationType();
        Integer quantityChange = req.getQuantityChange();
        Integer sourceWarehouse = req.getSourceWarehouse();
        Integer i = inventoryItemMapper.itemReversal(operationType,inventoryItemId,sourceWarehouse,quantityChange);
        System.out.println("i = " + i);
        return i;
    }

    @Override
    public Integer outbound(List<OrderItemPostReq> items) {
        items.forEach(item -> {
            Long itemId = item.getItemId();
            Integer quantity = item.getQuantity();
            Integer i = inventoryItemMapper.outbound(itemId,quantity);
            System.out.println("出库item更新 = " + i);
        });
        return 1;
    }

    @Override
    public Integer aftersale(ItemAftersaleChange item) {
        System.out.println("售后inventoryItem的item = " + item);
        OrderItem orderItem = orderItemMapper.selectById(item.getOrderItemId());
        InventoryItem inventoryItem = inventoryItemMapper.selectById(orderItem.getItemId());
        inventoryItem.setUpdateTime(new Date());
        inventoryItem.setTotalPieces(inventoryItem.getTotalPieces() - item.getQuantityChange());
        int i = inventoryItemMapper.updateById(inventoryItem);
        System.out.println("售后每个item对库存修改= " + i);
        return i;
    }

    @Override
    public ItemModelResp getItemModel(String modelNumber) {
        LambdaQueryWrapper<InventoryItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryItem::getModelNumber, modelNumber);
        InventoryItem inventoryItem = inventoryItemMapper.selectOne(wrapper);

        ItemModelResp resp = new ItemModelResp();
        BeanUtils.copyProperties(inventoryItem, resp);
        resp.setItemId(inventoryItem.getId());
        return resp;
    }


}




