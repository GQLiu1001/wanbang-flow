package com.wanbang.manager.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanbang.manager.common.DeliveryOrder;
import com.wanbang.manager.mapper.db1.OrderInfoMapper;
import com.wanbang.manager.mapper.db2.DeliveryOrderMapper;
import com.wanbang.manager.req.DeliveryOrderReq;
import com.wanbang.manager.resp.OrderDeliveryStatusResp;
import com.wanbang.manager.service.DeliveryOrderService;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.annotation.Resource;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
* @author 11965
* @description 针对表【delivery_order(配送订单表)】的数据库操作Service实现
* @createDate 2025-04-02 20:51:58
*/
@Service
public class DeliveryOrderServiceImpl extends ServiceImpl<DeliveryOrderMapper, DeliveryOrder>
    implements DeliveryOrderService{
    @Resource
    private DeliveryOrderMapper deliveryOrderMapper;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private RedissonClient redissonClient;


    private static final String ORDER_KEY = "delivery:orders"; // Redis 列表键

    @Override
    public IPage<DeliveryOrder> getPendingDeliveryOrders(int page, int size, String orderNo, String startStr, String endStr, String customerPhone) {

        IPage<DeliveryOrder> orderPage = new Page<>(page, size);
        return  deliveryOrderMapper.getPendingDeliveryOrders(orderPage,orderNo,customerPhone,startStr,endStr);
    }

    @Override
    public void postDispatchOrder(DeliveryOrderReq deliveryOrderReq) {
        // 创建订单实体并插入数据库
        DeliveryOrder deliveryOrder = new DeliveryOrder();
        deliveryOrder.setDeliveryStatus(2); // 状态2：待调度/可抢
        BeanUtils.copyProperties(deliveryOrderReq,deliveryOrder);
        int insert = deliveryOrderMapper.insert(deliveryOrder);
        System.out.println("创建派送订单，插入 = " + insert +" 行");

        // --- 使用 Redisson 添加到列表 ---
        RList<DeliveryOrderReq> orderList = redissonClient.getList(ORDER_KEY);
        orderList.add(deliveryOrderReq); // add() 通常添加到列表末尾（相当于 rightPush）
        // 如果你严格需要司机端从左侧弹出（leftPop），可能需要根据队列行为选择 addFirst() 或 addLast()
        // --- 结束改动 ---

        System.out.println("订单已派发并存入Redis: " + deliveryOrderReq);
    }


    @Override
    public OrderDeliveryStatusResp getDispatchOrderStatus(Long id) {
        DeliveryOrder deliveryOrder = deliveryOrderMapper.selectById(id);
        System.out.println("根据订单id查询到的deliveryOrder = " + deliveryOrder);
        OrderDeliveryStatusResp orderDeliveryStatusResp = new OrderDeliveryStatusResp();
        BeanUtils.copyProperties(deliveryOrder,orderDeliveryStatusResp);
        return orderDeliveryStatusResp;
    }

    @GlobalTransactional(rollbackFor = Exception.class)
    @Override
    public void updateDispatchStatus(Long id, Integer status, Long driverId) {
        DeliveryOrder deliveryOrder = deliveryOrderMapper.selectById(id);
        orderInfoMapper.changeDispatchStatusTo(deliveryOrder.getOrderNo(),status);

        deliveryOrder.setDeliveryStatus(status);
        if (driverId != null) {
            deliveryOrder.setDriverId(driverId);
        }
        System.out.println("更新派送状态更改的deliveryOrder = " + deliveryOrder);
        int i = deliveryOrderMapper.updateById(deliveryOrder);
        System.out.println("更新派送状态影响的行数 = " + i);

    }

    @GlobalTransactional(rollbackFor = Exception.class)
    @Override
    public void cancelDispatch(Long id, Long operatorId) {
        DeliveryOrder deliveryOrder = deliveryOrderMapper.selectById(id);
        if (deliveryOrder == null) {
            System.err.println("取消派送失败: 订单未找到, id=" + id);
            return; // 或抛出异常
        }

        // 检查订单是否处于可取消的状态 (例如，仍为状态 2)
        if (deliveryOrder.getDeliveryStatus() != 2) {
            System.err.println("订单 " + deliveryOrder.getOrderNo() + " 状态为 " + deliveryOrder.getDeliveryStatus() + ", 不能取消派送。");
            // 根据业务逻辑可能需要抛出异常
            return;
        }

        deliveryOrder.setDeliveryStatus(5); // 状态 5：已取消
        deliveryOrderMapper.updateById(deliveryOrder);
        orderInfoMapper.changeDispatchStatusTo0(deliveryOrder.getOrderNo()); // 假设 0 代表 orderInfo 中的取消/初始状态

        // --- 使用 Redisson 从 Redis 移除 ---
        RList<DeliveryOrderReq> orderList = redissonClient.getList(ORDER_KEY);
        // 创建一个临时的 Req 对象用于匹配（假设 orderNo 是关键标识符）
        DeliveryOrderReq reqToRemove = new DeliveryOrderReq();
        reqToRemove.setOrderNo(deliveryOrder.getOrderNo());
        // 如果你的 equals/hashCode 依赖其他字段，也需要设置它们

        // 移除对象。这仍然依赖于 DeliveryOrderReq 中基于 orderNo (或其他相关字段)
        // 正确实现了 equals/hashCode
        boolean removed = orderList.remove(reqToRemove);

        // --- 备选方案：通过遍历移除（效率较低，但可绕过 equals 问题）---
         /*
         String orderNoToRemove = deliveryOrder.getOrderNo();
         boolean removed = false;
         // 注意：对于 Redis List，遍历 get(i) 可能效率不高
         for (int i = 0; i < orderList.size(); i++) {
             // 获取元素时要小心潜在的并发修改问题，虽然 cancelDispatch 可能频率较低
             try {
                 if (orderList.get(i).getOrderNo().equals(orderNoToRemove)) {
                     orderList.remove(i); // 按索引移除
                     removed = true;
                     break;
                 }
             } catch (IndexOutOfBoundsException e) {
                 // 处理并发移除导致索引失效的情况
                 System.err.println("并发移除导致索引失效，取消操作可能需要重试或采取其他策略。 OrderNo: " + orderNoToRemove);
                 break; // 退出循环
             }
         }
         */
        // --- 结束备选方案 ---

        if (removed) {
            System.out.println("订单已取消并从Redis移除: " + id + ", OrderNo: " + deliveryOrder.getOrderNo());
        } else {
            // 如果订单状态确实是2，但移除失败，可能意味着 Redis 列表和数据库状态不一致
            System.out.println("订单已取消，但在Redis列表中未找到或移除失败: " + id + ", OrderNo: " + deliveryOrder.getOrderNo());
        }
    }
}




