package com.wanbang.console.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wanbang.console.common.OrderInfo;
import com.wanbang.console.dto.OrderInfoDTO;
import com.wanbang.console.dto.SalesInfoDTO;
import com.wanbang.console.resp.TodaySaleAmountResp;
import com.wanbang.console.resp.TopSoldItemsResp;
import com.wanbang.console.resp.TotalSaleAmountResp;

import java.util.List;

/**
* @author 11965
* @description 针对表【order_info(订单主表)】的数据库操作Mapper
* @createDate 2025-03-03 08:41:33
* @Entity com.wanbang.common.OrderInfo
*/
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {
    List<TopSoldItemsResp> TopSales();

    SalesInfoDTO getTopSalesTrend(String date);

    TodaySaleAmountResp getTodaySales(String dateStr);

    TotalSaleAmountResp getTotalSaleAmount();

    IPage<OrderInfoDTO> getOrderList(IPage<OrderInfoDTO> pageParam, String startStr, String endStr, String customerPhone);
}




