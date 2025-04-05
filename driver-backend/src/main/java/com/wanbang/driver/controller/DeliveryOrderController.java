package com.wanbang.driver.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wanbang.driver.common.DeliveryOrder;
import com.wanbang.driver.common.Result;
import com.wanbang.driver.dto.DeliveryOrderDTO;
import com.wanbang.driver.resp.DeliveryOrderListResp;
import com.wanbang.driver.service.DeliveryOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/order")
@Tag(name ="订单相关接口")
@RestController
public class DeliveryOrderController {
    @Resource
    private DeliveryOrderService deliveryOrderService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Operation(summary = "获取订单列表")
    @GetMapping("/list")
    public Result<DeliveryOrderListResp> getOrderList(@RequestParam(value = "page",defaultValue = "1") int page ,
                                                      @RequestParam(value = "size",defaultValue = "10") int size ,
                                                      @RequestParam(value = "status",required = false) Integer orderStatus) {
        IPage<DeliveryOrderDTO> resp = deliveryOrderService.getOrderList(page,size,orderStatus);
        DeliveryOrderListResp deliveryOrderListResp = new DeliveryOrderListResp();
        deliveryOrderListResp.setTotal(resp.getTotal());
        deliveryOrderListResp.setRecords(resp.getRecords());
        return Result.success(deliveryOrderListResp);
    }

    @Operation(summary = "获取可用新订单")
    @GetMapping("/newOrders")
    public Result<List<DeliveryOrder>> getNewOrders() {
        List<DeliveryOrder> newOrders = deliveryOrderService.getAvailableOrders();
        return Result.success(newOrders);
    }

    @Operation(summary = "司机抢单")
    @GetMapping("/robNewOrder/{driverId}/{orderNo}")
    public Result<Boolean> robNewOrder(@PathVariable("driverId") Long driverId, @PathVariable("orderNo") String orderNo) {
        Boolean flag = deliveryOrderService.robNewOrder(driverId, orderNo);
        if (flag) {
            return Result.success();
        }else {
            return Result.fail();
        }
    }

    @Operation(summary = "完成订单")
    @PostMapping("/complete")
    public Result complete(@RequestParam("orderId") Long orderId){
        deliveryOrderService.completeOrder(orderId);
        return Result.success();
    }

    @Operation(summary = "取消订单")
    @PostMapping("/cancel")
    public Result cancel(@RequestParam("orderId") Long orderId,
                         @RequestParam(value = "cancelReason",required = false)String cancelReason){
        deliveryOrderService.cancelOrder(orderId);
        return Result.success();
    }
}
