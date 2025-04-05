package com.wanbang.console.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanbang.console.common.ItemAftersaleChange;
import com.wanbang.console.common.OrderAftersaleLog;
import com.wanbang.console.mapper.OrderAftersaleLogMapper;
import com.wanbang.console.req.AftersalePostReq;
import com.wanbang.console.resp.AftersaleLogDetailResp;
import com.wanbang.console.service.OrderAftersaleLogService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* @author 11965
* @description 针对表【order_aftersale_log(订单售后日志表)】的数据库操作Service实现
* @createDate 2025-03-03 08:41:33
*/
@Service
public class OrderAftersaleLogServiceImpl extends ServiceImpl<OrderAftersaleLogMapper, OrderAftersaleLog>
    implements OrderAftersaleLogService{
    @Resource
    private OrderAftersaleLogMapper orderAftersaleLogMapper;

    @Override
    public Integer addLog(AftersalePostReq req, ItemAftersaleChange item) {
        OrderAftersaleLog orderAftersaleLog = new OrderAftersaleLog();
        BeanUtils.copyProperties(req, orderAftersaleLog);
        orderAftersaleLog.setOrderItemId(item.getOrderItemId());
        BeanUtils.copyProperties(item, orderAftersaleLog);
        orderAftersaleLog.setCreateTime(new Date());
        orderAftersaleLog.setUpdateTime(new Date());
        orderAftersaleLog.setAftersaleOperator(StpUtil.getLoginIdAsLong());
        System.out.println("orderAftersaleLog = " + orderAftersaleLog);
        int i = orderAftersaleLogMapper.insert(orderAftersaleLog);
        System.out.println("对售后订单的每次修改 = " + i);
        return i;
    }

    @Override
    public void changeStatus(Long id) {
        OrderAftersaleLog orderAftersaleLog = orderAftersaleLogMapper.selectById(id);
        orderAftersaleLog.setAftersaleStatus(2);
        orderAftersaleLog.setUpdateTime(new Date());
        orderAftersaleLogMapper.updateById(orderAftersaleLog);
    }

    @Override
    public List<AftersaleLogDetailResp> getAftersaleList(Integer orderId) {
        List<AftersaleLogDetailResp> respList = new ArrayList<>();
        List<OrderAftersaleLog> orderAftersaleLogs = orderAftersaleLogMapper.selectList(new LambdaQueryWrapper<OrderAftersaleLog>().eq(OrderAftersaleLog::getOrderId, orderId));
        orderAftersaleLogs.forEach(orderAftersaleLog -> {
            AftersaleLogDetailResp resp = new AftersaleLogDetailResp();
            BeanUtils.copyProperties(orderAftersaleLog, resp);
            respList.add(resp);
        });
        System.out.println("respList = " + respList);
        return respList;
    }
}




