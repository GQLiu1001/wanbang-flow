package com.wanbang.manager.mapper.db2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wanbang.manager.common.DeliveryOrder;

/**
* @author 11965
* @description 针对表【delivery_order(配送订单表)】的数据库操作Mapper
* @createDate 2025-04-02 20:51:58
* @Entity com.wanbang.manager.common.DeliveryOrder
*/
public interface DeliveryOrderMapper extends BaseMapper<DeliveryOrder> {


    IPage<DeliveryOrder> getPendingDeliveryOrders(IPage<DeliveryOrder> orderPage, String orderNo, String customerPhone, String startStr, String endStr);
}




