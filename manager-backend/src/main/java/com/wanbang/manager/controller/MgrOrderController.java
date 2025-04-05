package com.wanbang.manager.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wanbang.manager.common.DeliveryOrder;
import com.wanbang.manager.common.Result;
import com.wanbang.manager.req.DeliveryOrderReq;
import com.wanbang.manager.resp.DeliveryOrderResp;
import com.wanbang.manager.resp.OrderDeliveryStatusResp;
import com.wanbang.manager.service.DeliveryOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;


@Tag(name = "后台订单相关接口")
@RequestMapping("/api/delivery/order")
@RestController
public class MgrOrderController {

    @Resource
    private DeliveryOrderService deliveryOrderService;
    @Operation(summary = "获取待派送订单列表")
    @GetMapping
    public Result<DeliveryOrderResp> getPendingDeliveryOrders(@RequestParam(value = "page",defaultValue = "1") int page ,
                                                  @RequestParam(value = "size",defaultValue = "10") int size ,
                                                  @RequestParam(value = "orderNo",required = false) String orderNo,
                                                  @RequestParam(value = "customerPhone",required = false)String customerPhone,
                                                  @RequestParam(value = "startTime", required = false) String startTime,
                                                  @RequestParam(value = "endTime", required = false) String endTime) {
        String startStr = null;
        String endStr = null;
        if (startTime != null) {

            startStr = startTime.substring(0, 10);
            System.out.println("startStr = " + startStr);
        }
        if (endTime != null) {

            endStr = endTime.substring(0, 10);
            System.out.println("endStr = " + endStr);
        }
        IPage<DeliveryOrder> resp = deliveryOrderService.getPendingDeliveryOrders(page,size,orderNo, startStr, endStr,customerPhone);
        DeliveryOrderResp deliveryOrderResp = new DeliveryOrderResp();
        deliveryOrderResp.setTotal(resp.getTotal());
        deliveryOrderResp.setRecords(resp.getRecords());
        System.out.println("触发获取待派送订单列表");
        return Result.success(deliveryOrderResp);
    }
    @Operation(summary = "创建派送订单")
    @PostMapping
    public Result postDispatchOrder(@RequestBody DeliveryOrderReq deliveryOrderReq) {
        deliveryOrderService.postDispatchOrder(deliveryOrderReq);
        return Result.success();
    }
    @Operation(summary = "获取派送状态")
    @GetMapping("/{id}/status")
    public Result<OrderDeliveryStatusResp> getDispatchOrderStatus(@PathVariable("id") Long id) {
        OrderDeliveryStatusResp resp = deliveryOrderService.getDispatchOrderStatus(id);
        return Result.success(resp);
    }
    @Operation(summary = "更新派送状态")
    @PutMapping("/{id}/status")
    public Result updateDispatchStatus(@PathVariable("id") Long id,
                                    @RequestParam("status") Integer status,
                                    @RequestParam("driverId") Long driverId) {
        deliveryOrderService.updateDispatchStatus(id,status,driverId);
        return Result.success();
    }
    @Operation(summary = "取消派送")
    @PostMapping("/{id}/status")
    public Result cancelDispatch(@PathVariable("id") Long id,
                                    @RequestParam("operatorId") Long operatorId) {
        deliveryOrderService.cancelDispatch(id,operatorId);
        return Result.success();
    }

}
