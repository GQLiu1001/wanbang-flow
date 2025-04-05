package com.wanbang.console.controller;

import com.wanbang.console.common.Result;
import com.wanbang.console.req.AftersalePostReq;
import com.wanbang.console.resp.AftersaleLogDetailResp;
import com.wanbang.console.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "售后相关接口")
@RequestMapping("/api/aftersales")
@RestController
@CrossOrigin
public class AftersaleController {
    @Resource
    OrderAftersaleLogService orderAftersaleLogService;
    @Resource
    OrderInfoService orderInfoService;
    @Resource
    OrderItemService orderItemService;
    @Resource
    InventoryLogService inventoryLogService;
    @Resource
    InventoryItemService inventoryItemService;


    @Transactional(rollbackFor = Exception.class)
    @Operation(summary = "创建售后")
    @PostMapping
    public Result aftersale(@RequestBody AftersalePostReq req) {
        req.getItems().forEach(item -> {
            BigDecimal amountChange = item.getAmountChange();
            //修改orderInfo 的aftersale_status 赋值为1 修改adjusted_amount order_update_time
            Integer i = orderInfoService.aftersale(amountChange, req.getOrderId());
            System.out.println("i = " + i);
            //修改orderItem 的 adjusted_quantity  subtotal  update_time
            Integer j = orderItemService.aftersale(item);
            System.out.println("j = " + j);
            //inventoryLog 增加 多退 or 少补
            Integer n = inventoryLogService.aftersale(item);
            System.out.println("n = " + n);
            //修改inventoryItem 的  total_pieces update_time
            Integer m = inventoryItemService.aftersale(item);
            System.out.println("m = " + m);
            //orderAftersaleLog 增加 记录
            Integer l = orderAftersaleLogService.addLog(req, item);
            System.out.println("l = " + l);
        });
        return Result.success();
    }

    @Operation(summary = "获得订单的售后记录")
    @GetMapping("/order/{orderId}")
    public Result<List<AftersaleLogDetailResp>> order(@PathVariable Integer orderId) {
        List<AftersaleLogDetailResp> resp = orderAftersaleLogService.getAftersaleList(orderId);
        System.out.println("resp = " + resp);
        return Result.success(resp);
    }

    @Operation(summary = "修改售后记录状态")
    @PutMapping("/{id}/status")
    public Result changeStatus( @PathVariable Long id) {
        //id是售后记录表的id
        orderAftersaleLogService.changeStatus(id);
        return Result.success();
    }
}
