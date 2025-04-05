package com.wanbang.console.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanbang.console.common.ItemAftersaleChange;
import com.wanbang.console.common.OrderItem;
import com.wanbang.console.enums.ResultCode;
import com.wanbang.console.exception.WanbangException;
import com.wanbang.console.mapper.OrderItemMapper;
import com.wanbang.console.req.AddOrderItemReq;
import com.wanbang.console.req.OrderItemChangeReq;
import com.wanbang.console.req.OrderItemPostReq;
import com.wanbang.console.service.OrderItemService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author 11965
* @description 针对表【order_item(订单项表)】的数据库操作Service实现
* @createDate 2025-03-03 08:41:33
*/
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem>
    implements OrderItemService{
    @Resource
    private OrderItemMapper orderItemMapper;
    @Override
    public Integer outbound(List<OrderItemPostReq> items,Long orderId) {
        items.forEach(item->{
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            BeanUtils.copyProperties(item,orderItem);
            orderItem.setAdjustedQuantity(item.getQuantity());
            orderItem.setCreateTime(new Date());
            orderItem.setUpdateTime(new Date());
            Integer i = orderItemMapper.insert(orderItem);
            System.out.println("order物品插入= " + i);
        });
        return 1;
    }

    @Override
    public List<OrderItem> getDetailList(Long id) {
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId,id);
        List<OrderItem> orderItems = orderItemMapper.selectList(wrapper);
        System.out.println("orderItems = " + orderItems);
        return orderItems;
    }

    @Override
    public Integer updateOrderItem(Long id, OrderItemChangeReq orderItemChangeReq) {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(id);
        orderItem.setAdjustedQuantity(orderItemChangeReq.getQuantity());
        orderItem.setPricePerPiece(orderItemChangeReq.getPricePerPiece());
        orderItem.setSubtotal(orderItemChangeReq.getSubtotal());
        orderItem.setUpdateTime(new Date());
        System.out.println("修改后的orderItem = " + orderItem);
        int i = orderItemMapper.updateById(orderItem);
        System.out.println("i = " + i);
        return i;
    }

    @Override
    public Integer removeSubItem(Long id) {
        QueryWrapper<OrderItem> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id",id);
        int delete = orderItemMapper.delete(wrapper);
        System.out.println("delete = " + delete);
        return delete;
    }

    @Override
    public Integer addSubItem(AddOrderItemReq addOrderItemReq, Long id) {
        OrderItem orderItem = new OrderItem();
        BeanUtils.copyProperties(addOrderItemReq,orderItem);
        orderItem.setAdjustedQuantity(addOrderItemReq.getQuantity());
        orderItem.setCreateTime(new Date());
        orderItem.setUpdateTime(new Date());
        orderItem.setOrderId(id);
        int i = orderItemMapper.insert(orderItem);
        System.out.println("i = " + i);
        return i;
    }

    @Override
    public void check(Long id, Long itemId, String modelNumber) {
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId,id);
        wrapper.eq(OrderItem::getItemId,itemId);
        OrderItem item = orderItemMapper.selectOne(wrapper);
        if (item != null) {
            throw new WanbangException(ResultCode.FAIL);
        }
    }

    @Override
    public Integer aftersale(ItemAftersaleChange item) {
        Long itemId =  item.getOrderItemId();
        OrderItem orderItem = orderItemMapper.selectById(itemId);
        orderItem.setAdjustedQuantity(orderItem.getAdjustedQuantity() + item.getQuantityChange());
        orderItem.setSubtotal(orderItem.getSubtotal().add(item.getAmountChange()));
        orderItem.setUpdateTime(new Date());
        int i = orderItemMapper.updateById(orderItem);
        System.out.println("售后子订单对orderItem的每次修改 = " + i);
        return i;
    }
}




