package com.wanbang.driver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wanbang.driver.common.DeliveryOrder;
import com.wanbang.driver.dto.DeliveryOrderDTO;

/**
* @author 11965
* @description 针对表【delivery_order(配送订单表)】的数据库操作Mapper
* @createDate 2025-04-02 20:51:11
* @Entity com.wanbang.delivery.common.DeliveryOrder
*/
public interface DeliveryOrderMapper extends BaseMapper<DeliveryOrder> {

    IPage<DeliveryOrderDTO> getOrderListByStuts(IPage<DeliveryOrderDTO> pageInfo, Integer orderStatus);
}




