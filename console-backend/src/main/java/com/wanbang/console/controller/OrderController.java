package com.wanbang.console.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wanbang.console.common.*;
import com.wanbang.console.dto.OrderInfoDTO;
import com.wanbang.console.req.*;
import com.wanbang.console.resp.OrderDetailResp;
import com.wanbang.console.resp.OrderListResp;
import com.wanbang.console.service.InventoryItemService;
import com.wanbang.console.service.InventoryLogService;
import com.wanbang.console.service.OrderInfoService;
import com.wanbang.console.service.OrderItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@CrossOrigin
@Tag(name = "订单相关接口")
@RequestMapping("/api/orders")
@RestController
public class OrderController {
    @Resource
    private InventoryItemService inventoryItemService;
    @Resource
    private InventoryLogService inventoryLogService;
    @Resource
    private OrderInfoService orderInfoService;
    @Resource
    private OrderItemService orderItemService;

    @Transactional(rollbackFor = Exception.class)
    @Operation(summary = "创建订单")
    @PostMapping
    public Result addOrder(@RequestBody OrderPostReq orderPostReq) {
        //order_info表单需要:
        //customer_phone operator_id order_remark  total_amount  adjusted_amount
        //aftersale_status
        //order_item表单需要:
        //order_id item_id model_number  quantity price_per_piece subtotal(小记金额)
        //adjusted_quantity
        //创建一个请求之后，先向数据库插入一个log 出库 在log中也要同时修改item
        //log 出库需要什么? inventory_item_id operation_type quantity_change operator_id source_warehouse remark
        //item 修改需要什么? inventory_item_id(id) quantity_change(total_pieces)
        //再向order_info 和 order_item 插入记录
        Integer i = inventoryLogService.outbound(orderPostReq.getItems());
        Integer j = inventoryItemService.outbound(orderPostReq.getItems());
        Long orderId = orderInfoService.outbound(orderPostReq);
        Integer l = orderItemService.outbound(orderPostReq.getItems(), orderId);
        return Result.success();
    }

    @Operation(summary = "查询订单列表")
    @GetMapping
    public Result<OrderListResp> getAllOrders(
            @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "start_time", required = false) String startTime, @RequestParam(value = "end_time", required = false) String endTime,
            @RequestParam(value = "customer_phone", required = false) String customerPhone
    ) {
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

        IPage<OrderInfoDTO> resp = orderInfoService.getOrderList(page, size, startStr, endStr, customerPhone);
        OrderListResp orderListResp = new OrderListResp();
        orderListResp.setItems(resp.getRecords());
        orderListResp.setTotal(resp.getTotal());
        orderListResp.setPage(resp.getCurrent());
        orderListResp.setSize(resp.getSize());
        System.out.println("orderListResp = " + orderListResp);
        return Result.success(orderListResp);

    }

    @Operation(summary = "查询订单详细")
    @GetMapping("/{id}")
    public Result<OrderDetailResp> orderDetail(@PathVariable("id") Long id) {
        OrderDetailResp resp = new OrderDetailResp();
        OrderInfo byId = orderInfoService.getById(id);
        System.out.println("byId = " + byId);
        BeanUtils.copyProperties(byId, resp);
        List<OrderItem> list = orderItemService.getDetailList(id);
        System.out.println("list = " + list);
        resp.setItems(list);
        return Result.success(resp);
    }

    @Operation(summary = "修改订单(不包括订单项的变更)")
    @PutMapping("/{id}")
    public Result updateOrder(@PathVariable("id") Long id, @RequestBody OrderChangeReq orderChangeReq) {
        OrderInfo byId = orderInfoService.getById(id);
        BeanUtils.copyProperties(orderChangeReq, byId);
        byId.setOrderUpdateTime(new Date());
        orderInfoService.updateById(byId);
        return Result.success();

    }

    @Transactional(rollbackFor = Exception.class)
    @Operation(summary = "订单项变：更修改指定订单项的信息")
    @PutMapping("/items/{itemId}")
    public Result updateOrderItem(@PathVariable("itemId") Long id,
                                  @RequestBody OrderItemChangeReq orderItemChangeReq) {
        OrderItem originItem = orderItemService.getById(id);
        System.out.println("originItem = " + originItem);
        //提供 quantity price_per_piece subtotal
        //订单项变更： 需要冲正
        //影响的表单:
        // order_item 直接改quantity price_per_piece subtotal
        Integer i = orderItemService.updateOrderItem(id, orderItemChangeReq);
        // order_info的adjusted_amount(subtotal) order_update_time
        Integer j = orderInfoService.updateOrderItem(originItem, orderItemChangeReq.getSubtotal());
        // inventory_item 的total_pieces
        //上一次的数量
        InventoryLog reversalLog = new InventoryLog();
        InventoryLog reversalLog2 = new InventoryLog();
        Integer originQuantity = originItem.getAdjustedQuantity();
        System.out.println("originQuantity = " + originQuantity);
        Integer changeQuantity = orderItemChangeReq.getQuantity() - originQuantity;
        Integer changeQuantity2 = orderItemChangeReq.getQuantity() - originQuantity;
        reversalLog2.setOperationType(1);
        reversalLog.setOperationType(1);
        //当changeQuantity大于或者小于0的时候 冲正的类型是不一样的 入库和出库
        if (changeQuantity < 0) {
            changeQuantity = -changeQuantity;
            reversalLog2.setOperationType(2);
            reversalLog.setOperationType(2);
        }
        System.out.println("changeQuantity = " + changeQuantity);
        reversalLog2.setInventoryItemId(originItem.getItemId());
        reversalLog2.setQuantityChange(changeQuantity);
        InventoryItem item = inventoryItemService.getById(originItem.getItemId());
        reversalLog2.setSourceWarehouse(item.getWarehouseNum());
        Integer i1 = inventoryItemService.itemReversal(reversalLog2);
        System.out.println("i1 = " + i1);
        // inventory_log 冲正出库记录 ? 自己手动写一个?因为没有关于inventory_log的相关联系
        reversalLog.setInventoryItemId(originItem.getItemId());
        reversalLog.setQuantityChange(changeQuantity2);
        reversalLog.setOperatorId(StpUtil.getLoginIdAsLong());
        reversalLog.setRemark("出库的冲正记录");
        reversalLog.setUpdateTime(new Date());
        reversalLog.setCreateTime(new Date());
        //inventoryLogService.itemReversal这里的方法和inventoryItemService.itemReversal不一样
        Integer k = inventoryLogService.itemReversal(reversalLog);
        return Result.success();

    }

    @Transactional(rollbackFor = Exception.class)
    @Operation(summary = "添加订单项")
    @PostMapping("/{orderId}/items")
    public Result addOrderItem(@PathVariable("orderId") Long id, @RequestBody AddOrderItemReq addOrderItemReq) {
        orderItemService.check(id, addOrderItemReq.getItemId(), addOrderItemReq.getModelNumber());
        //根据orderId插入一个子单 需要修改order的subtotal 需要添加一条记录到item
        Integer i = orderItemService.addSubItem(addOrderItemReq, id);
        //info表单改金钱
        Integer j = orderInfoService.addSubInfo(id, addOrderItemReq.getSubtotal());
        //inventoryInfo加出库
        InventoryItem item = inventoryItemService.getById(addOrderItemReq.getItemId());
        List<OrderItemPostReq> list = new ArrayList<>();
        OrderItemPostReq orderItemPostReq = new OrderItemPostReq();
        orderItemPostReq.setItemId(addOrderItemReq.getItemId());
        orderItemPostReq.setQuantity(addOrderItemReq.getQuantity());
        orderItemPostReq.setSourceWarehouse(item.getWarehouseNum());
        list.add(orderItemPostReq);
        Integer outbound = inventoryLogService.outbound(list);
        //inventoryItem减去数量
        Integer outbound1 = inventoryItemService.outbound(list);
        return Result.success();
    }

    @Transactional(rollbackFor = Exception.class)
    @Operation(summary = "删除订单母单")
    @SaCheckRole("admin")
    @DeleteMapping("/{orderId}")
    public Result deleteOrder(@PathVariable("orderId") Long id) {
        Integer i = orderItemService.removeSubItem(id);
        //删除母单信息 and 相关子单
        boolean b = orderInfoService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "删除订单子单")
    @SaCheckRole("admin")
    @DeleteMapping("/items/{itemId}")
    public Result deleteOrderItem(@PathVariable("itemId") Long id) {
        //删除单个子单
        boolean b = orderItemService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "更改订单派送状态")
    @PutMapping("/{id}/dispatch_status")
    public Result dispatchOrderStatus(@PathVariable("id") Long id) {

        OrderInfo byId = orderInfoService.getById(id);
        byId.setDispatchStatus(1);
        System.out.println(byId);
        orderInfoService.updateById(byId);
        return Result.success();
    }

}
