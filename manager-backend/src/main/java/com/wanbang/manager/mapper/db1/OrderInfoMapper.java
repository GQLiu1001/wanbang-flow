package com.wanbang.manager.mapper.db1;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wanbang.manager.common.OrderInfo;

/**
* @author 11965
* @description 针对表【order_info(订单主表)】的数据库操作Mapper
* @createDate 2025-04-02 20:48:17
* @Entity com.wanbang.manager.common.OrderInfo
*/
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    void changeDispatchStatusTo0(String orderNo);

    void changeDispatchStatusTo(String orderNo, Integer status);
}




