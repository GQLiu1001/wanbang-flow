package com.wanbang.console.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanbang.console.common.OrderInfo;
import com.wanbang.console.common.OrderItem;
import com.wanbang.console.config.RabbitMQConfig;
import com.wanbang.console.dto.OrderInfoDTO;
import com.wanbang.console.dto.SalesInfoDTO;
import com.wanbang.console.mapper.InventoryItemMapper;
import com.wanbang.console.mapper.OrderInfoMapper;
import com.wanbang.console.req.OrderPostReq;
import com.wanbang.console.resp.SalesTrendResp;
import com.wanbang.console.resp.TodaySaleAmountResp;
import com.wanbang.console.resp.TopSoldItemsResp;
import com.wanbang.console.resp.TotalSaleAmountResp;
import com.wanbang.console.service.OrderInfoService;
import com.wanbang.console.util.YearMonthUtil;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* @author 11965
* @description 针对表【order_info(订单主表)】的数据库操作Service实现
* @createDate 2025-03-03 08:41:33
*/
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo>
    implements OrderInfoService{
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private InventoryItemMapper inventoryItemMapper;
    @Override
    public List<TopSoldItemsResp> TopSales() {
        List<TopSoldItemsResp> topSoldItemsResp = orderInfoMapper.TopSales();
        System.out.println(topSoldItemsResp);
        return topSoldItemsResp;
    }

    @Override
    public List<SalesTrendResp> TopSalesTrend(Integer year, Integer month , Integer length) {
        List<String> dates = YearMonthUtil.format(year, month, length);
        System.out.println(dates);
        List<SalesTrendResp> salesTrendResps = new ArrayList<>();
        dates.forEach(date -> {
            System.out.println(date);
            SalesInfoDTO salesInfoDTO = orderInfoMapper.getTopSalesTrend(date);
            SalesTrendResp salesTrendResp = new SalesTrendResp();
            salesTrendResp.setDates(date);
            if (salesInfoDTO != null) {
                salesTrendResp.setAdjustedAmount(salesInfoDTO.getAdjustedAmount());
            }else {
                salesTrendResp.setAdjustedAmount(0);
            }
            if (salesInfoDTO != null) {
                salesTrendResp.setAdjustedQuantity(salesInfoDTO.getAdjustedQuantity());
            }else {
                salesTrendResp.setAdjustedQuantity(0);
            }
            salesTrendResps.add(salesTrendResp);
            System.out.println(salesTrendResps);
        });
        System.out.println(salesTrendResps);
        return salesTrendResps;
    }





    @Override
    public TodaySaleAmountResp getTodaySales(String dateStr) {
        System.out.println(dateStr);
        TodaySaleAmountResp resp = orderInfoMapper.getTodaySales(dateStr);
        System.out.println(resp);
        return resp;
    }

    @Override
    public TotalSaleAmountResp getTotalSales() {
        TotalSaleAmountResp resp = orderInfoMapper.getTotalSaleAmount();
        System.out.println(resp);
        return resp;
    }

    @Override
    public Long outbound(OrderPostReq orderPostReq) {
        OrderInfo orderInfo = new OrderInfo();
        System.out.println("orderPostReq = " + orderPostReq);
        BeanUtils.copyProperties(orderPostReq, orderInfo);
        orderInfo.setAdjustedAmount(orderPostReq.getTotalAmount());
        orderInfo.setTotalAmount(orderPostReq.getTotalAmount());
        orderInfo.setOrderNo("ORD" + IdWorker.getId());
        orderInfo.setDispatchStatus(0);
        orderInfo.setOrderCreateTime(new Date());
        orderInfo.setOrderUpdateTime(new Date());
        int i = orderInfoMapper.insert(orderInfo);
        System.out.println("order记录插入= " + i);
        //mybatis plus自动回填
        return orderInfo.getId();
    }

    @Override
    public IPage<OrderInfoDTO> getOrderList(Integer page, Integer size, String startStr, String endStr, String customerPhone) {
        IPage<OrderInfoDTO> pageParam = new Page<>(page, size);
        IPage<OrderInfoDTO> resp = orderInfoMapper.getOrderList(pageParam,startStr,endStr,customerPhone);
        System.out.println("resp = " + resp);
        return resp;
    }

    @Override
    public Integer updateOrderItem(OrderItem originItem, BigDecimal subtotal) {
        Long orderId = originItem.getOrderId();
        //准备要修改的originInfo
        OrderInfo originInfo = orderInfoMapper.selectById(orderId);
        // order_info的adjusted_amount(subtotal) order_update_time
        //原本该记录的金钱 originSubtotal
        BigDecimal originSubtotal = originItem.getSubtotal();
        //修改后的记录的金钱 subtotal
        //相差 originSubtotal-subtotal
        BigDecimal changeSubtotal =
                originSubtotal.subtract(subtotal);
        //如果大于0 orderInfo加差价 如果小于0 orderInfo减差价
        //获取记录的钱
        BigDecimal adjustedAmount = originInfo.getAdjustedAmount();
        //修改原来记录的时间
        originInfo.setOrderUpdateTime(new Date());
        //是根据上一次操作的数值 ！！！
        originInfo.setAdjustedAmount(adjustedAmount.subtract(changeSubtotal));

        int i = orderInfoMapper.updateById(originInfo);
        System.out.println("i = " + i);

        return i;
    }

    @Override
    public Integer addSubInfo(Long id, BigDecimal subtotal) {
        OrderInfo orderInfo = orderInfoMapper.selectById(id);
        orderInfo.setAdjustedAmount(orderInfo.getAdjustedAmount().add(subtotal));
        orderInfo.setOrderUpdateTime(new Date());
        int i = orderInfoMapper.updateById(orderInfo);
        System.out.println("i = " + i);
        return i;
    }

    @Override
    public Integer aftersale(BigDecimal amountChange,Long orderId) {
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        Integer aftersaleStatus = orderInfo.getAftersaleStatus();
        if (aftersaleStatus == null) {
            aftersaleStatus = 1;
            orderInfo.setAftersaleStatus(aftersaleStatus);
        }
        orderInfo.setAdjustedAmount(orderInfo.getAdjustedAmount().add(amountChange));
        int i = orderInfoMapper.updateById(orderInfo);
        System.out.println("售后子订单对orderInfo的AdjustedAmount每次修改 = " + i);
        return i;
    }

    @RabbitListener(queues = RabbitMQConfig.CHANGE_STATUS_QUEUE)
    public void changeStatus(String orderNo) {
        System.out.println("开始处理rabbitmq: " + orderNo);
        try {
            LambdaUpdateWrapper<OrderInfo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(OrderInfo::getOrderNo, orderNo);
            updateWrapper.set(OrderInfo::getOrderUpdateTime, new Date());
            updateWrapper.set(OrderInfo::getDispatchStatus, 2);
            orderInfoMapper.update(updateWrapper);
        } catch (Exception e) {
            System.err.println("修改订单状态失败: " + e.getMessage());
        }
    }
}




