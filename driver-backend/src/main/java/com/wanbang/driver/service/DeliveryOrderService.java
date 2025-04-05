package com.wanbang.driver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wanbang.driver.common.DeliveryOrder;
import com.wanbang.driver.dto.DeliveryOrderDTO;

import java.util.List;

/**
* @author 11965
* @description 针对表【delivery_order(配送订单表)】的数据库操作Service
* @createDate 2025-04-02 20:51:11
*/
public interface DeliveryOrderService extends IService<DeliveryOrder> {

    IPage<DeliveryOrderDTO> getOrderList(int page, int size, Integer orderStatus);

    Boolean robNewOrder(Long driverId, String orderNo);

    List<DeliveryOrder> getAvailableOrders();

    void completeOrder(Long orderId);

    void cancelOrder(Long orderId);
}
