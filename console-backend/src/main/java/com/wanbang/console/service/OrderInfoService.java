package com.wanbang.console.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wanbang.console.common.OrderInfo;
import com.wanbang.console.common.OrderItem;
import com.wanbang.console.dto.OrderInfoDTO;
import com.wanbang.console.req.OrderPostReq;
import com.wanbang.console.resp.SalesTrendResp;
import com.wanbang.console.resp.TodaySaleAmountResp;
import com.wanbang.console.resp.TopSoldItemsResp;
import com.wanbang.console.resp.TotalSaleAmountResp;

import java.math.BigDecimal;
import java.util.List;

/**
* @author 11965
* @description 针对表【order_info(订单主表)】的数据库操作Service
* @createDate 2025-03-03 08:41:33
*/
public interface OrderInfoService extends IService<OrderInfo> {
    List<TopSoldItemsResp> TopSales();

    List<SalesTrendResp> TopSalesTrend(Integer year, Integer month, Integer length);

    TodaySaleAmountResp getTodaySales(String dateStr);

    TotalSaleAmountResp getTotalSales();
    Long outbound(OrderPostReq orderPostReq);

    IPage<OrderInfoDTO> getOrderList(Integer page, Integer size, String startStr, String endStr, String customerPhone);


    Integer updateOrderItem(OrderItem originItem, BigDecimal subtotal);

    Integer addSubInfo(Long id, BigDecimal subtotal);

    Integer aftersale(BigDecimal amountChange,Long orderId);
}
