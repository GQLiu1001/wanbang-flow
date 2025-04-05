package com.wanbang.driver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanbang.console.common.OrderInfo;
import com.wanbang.console.mapper.OrderInfoMapper;
import com.wanbang.driver.common.DeliveryOrder;
import com.wanbang.driver.common.DriverInfo;
import com.wanbang.driver.config.RabbitMQConfig;
import com.wanbang.driver.dto.DeliveryOrderDTO;
import com.wanbang.driver.mapper.DeliveryOrderMapper;
import com.wanbang.driver.mapper.DriverInfoMapper;
import com.wanbang.driver.service.DeliveryOrderService;

import com.wanbang.manager.req.DeliveryOrderReq;
import com.wanbang.manager.util.UserContextHolder;
import io.seata.spring.annotation.GlobalLock;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.annotation.Resource;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 11965
 * @description 针对表【delivery_order(配送订单表)】的数据库操作Service实现
 * @createDate 2025-04-02 20:51:11
 */
@Service
public class DeliveryOrderServiceImpl extends ServiceImpl<DeliveryOrderMapper, DeliveryOrder>
        implements DeliveryOrderService {
    @Resource
    private OrderInfoMapper orderInfoMapper;

    @Resource
    private DriverInfoMapper driverInfoMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private DeliveryOrderMapper deliveryOrderMapper;

    @Resource
    private RedisTemplate<String, DeliveryOrder> redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    private static final String ORDER_KEY = "delivery:orders"; // Redis 列表键
    private static final String LOCK_PREFIX = "lock:order:";   // 分布式锁前缀

    @Override
    public IPage<DeliveryOrderDTO> getOrderList(int page, int size, Integer orderStatus) {
        IPage<DeliveryOrderDTO> pageInfo = new Page<>(page, size);
        return deliveryOrderMapper.getOrderListByStuts(pageInfo, orderStatus);
    }

    // 司机抢单
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean robNewOrder(Long driverId, String orderNo) {
        if (driverId == null || orderNo == null || orderNo.isEmpty()) {
            throw new IllegalArgumentException("driverId 或 orderNo 不能为空");
        }

        // 定义分布式锁的 key
        String lockKey = LOCK_PREFIX + orderNo;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 尝试获取锁，等待 5 秒，锁持有时间 10 秒
            if (!lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                return false; // 获取锁失败，抢单失败
            }

            // 查询订单状态
            LambdaQueryWrapper<DeliveryOrder> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DeliveryOrder::getOrderNo, orderNo);
            queryWrapper.eq(DeliveryOrder::getDeliveryStatus, 2);
            DeliveryOrder order = deliveryOrderMapper.selectOne(queryWrapper);
            System.out.println("司机触发方法 司机ID"+ driverId);
            // 检查订单是否存在且可抢
            if (order == null || order.getDeliveryStatus() != 2) {
                return false; // 订单不存在或已被抢
            }
            if (order.getDriverId()!=null){
                return false;
            }
            // 更新订单状态为“配送中”（3）并绑定司机
            order.setDriverId(driverId);
            order.setDeliveryStatus(3);
            int rows = deliveryOrderMapper.updateById(order);
            if (rows == 0) {
                throw new RuntimeException("订单更新失败，orderNo: " + orderNo);
            }

            // 使用与存入相同的方式：Redisson的RList
            RList<DeliveryOrderReq> orderList = redissonClient.getList(ORDER_KEY);
            // 先找到要删除的项
            DeliveryOrderReq toRemove = null;
            for (DeliveryOrderReq req : orderList) {
                if (req.getOrderNo().equals(orderNo)) {
                    toRemove = req;
                    break;
                }
            }
            // 如果找到了，再删除
            if (toRemove != null) {
                orderList.remove(toRemove);
            }
            return true; // 抢单成功

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("抢单被中断", e);
        } finally {
            // 释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public List<DeliveryOrder> getAvailableOrders() {
        // 查询所有待派送状态(2)的订单
        LambdaQueryWrapper<DeliveryOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeliveryOrder::getDeliveryStatus, 2)
                .orderByDesc(DeliveryOrder::getCreateTime);

        // 可以根据需要限制返回数量
        return deliveryOrderMapper.selectList(queryWrapper);
    }
    @GlobalTransactional(rollbackFor = Exception.class)
    @Override
    public void cancelOrder(Long orderId) {
        DeliveryOrder deliveryOrder = deliveryOrderMapper.selectById(orderId);
        String orderNo = deliveryOrder.getOrderNo();
        LambdaUpdateWrapper<DeliveryOrder> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(DeliveryOrder::getId, orderId);
        wrapper.set(DeliveryOrder::getDeliveryStatus, 5);
        deliveryOrderMapper.update(null, wrapper);
//        LambdaUpdateWrapper<OrderInfo> wrapper1 = new LambdaUpdateWrapper<>();
//        wrapper1.eq(OrderInfo::getOrderNo, orderNo);
//        wrapper1.set(OrderInfo::getDispatchStatus, 0);
//        orderInfoMapper.update(null,wrapper1);
    }

    @Override
    public void completeOrder(Long orderId) {
        // 查询订单信息以获取司机ID和订单金额
        DeliveryOrder order = deliveryOrderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在: " + orderId);
        }

        // 更新订单状态为已完成(4)
        LambdaUpdateWrapper<DeliveryOrder> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(DeliveryOrder::getId, orderId);
        wrapper.set(DeliveryOrder::getDeliveryStatus, 4);
        deliveryOrderMapper.update(null, wrapper);
        // 发送加钱消息
        System.out.println("发送加钱消息: " + order);
        rabbitTemplate.convertAndSend(RabbitMQConfig.ADD_MONEY_EXCHANGE, RabbitMQConfig.ADD_MONEY_ROUTING_KEY, order);

        // 发送改状态消息
        String orderNo = order.getOrderNo();
        System.out.println("发送改状态消息: " + orderNo);
        rabbitTemplate.convertAndSend(RabbitMQConfig.CHANGE_STATUS_EXCHANGE, RabbitMQConfig.CHANGE_STATUS_ROUTING_KEY, orderNo);
    }

    @RabbitListener(queues = RabbitMQConfig.ADD_MONEY_QUEUE)
    public void addDriverMoney(DeliveryOrder order) {
        System.out.println("开始处理rabbitmq: " + order);
        try {
            Long driverId = order.getDriverId();
            BigDecimal deliveryFee = order.getDeliveryFee();
            if (driverId != null && deliveryFee != null) {
                DriverInfo driverInfo = driverInfoMapper.selectById(driverId);
                BigDecimal newMoney = driverInfo.getMoney().add(deliveryFee);
                LambdaUpdateWrapper<DriverInfo> wrapper = new LambdaUpdateWrapper<>();
                wrapper.eq(DriverInfo::getId, driverId);
                wrapper.set(DriverInfo::getMoney, newMoney);
                driverInfoMapper.update(null, wrapper);
                System.out.println("司机(" + driverId + ")完成订单(" + order.getOrderNo() + "), 增加余额: " + deliveryFee);
            }
        } catch (Exception e) {
            System.err.println("处理司机收益失败: " + e.getMessage());
        }
    }


}