package com.wanbang.manager.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wanbang.manager.common.DeliveryOrder;
import com.wanbang.manager.req.DeliveryOrderReq;
import com.wanbang.manager.resp.OrderDeliveryStatusResp;

/**
* @author 11965
* @description 针对表【delivery_order(配送订单表)】的数据库操作Service
* @createDate 2025-04-02 20:51:58
*/
public interface DeliveryOrderService extends IService<DeliveryOrder> {



    void postDispatchOrder(DeliveryOrderReq deliveryOrderReq);

    OrderDeliveryStatusResp getDispatchOrderStatus(Long id);

    void updateDispatchStatus(Long id, Integer status, Long driverId);

    void cancelDispatch(Long id, Long operatorId);

    IPage<DeliveryOrder> getPendingDeliveryOrders(int page, int size, String orderNo, String startStr, String endStr, String customerPhone);
}
