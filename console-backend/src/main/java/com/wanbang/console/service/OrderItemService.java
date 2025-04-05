package com.wanbang.console.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wanbang.console.common.ItemAftersaleChange;
import com.wanbang.console.common.OrderItem;
import com.wanbang.console.req.AddOrderItemReq;
import com.wanbang.console.req.OrderItemChangeReq;
import com.wanbang.console.req.OrderItemPostReq;

import java.util.List;

/**
* @author 11965
* @description 针对表【order_item(订单项表)】的数据库操作Service
* @createDate 2025-03-03 08:41:33
*/
public interface OrderItemService extends IService<OrderItem> {

    Integer outbound(List<OrderItemPostReq> items,Long orderId);

    List<OrderItem> getDetailList(Long id);

    Integer updateOrderItem(Long id, OrderItemChangeReq orderItemChangeReq);

    Integer removeSubItem(Long id);

    Integer addSubItem(AddOrderItemReq addOrderItemReq, Long id);

    void check(Long id, Long itemId, String modelNumber);

    Integer aftersale(ItemAftersaleChange item);
}
