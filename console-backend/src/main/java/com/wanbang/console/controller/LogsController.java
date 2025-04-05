package com.wanbang.console.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wanbang.console.common.InventoryItem;
import com.wanbang.console.common.InventoryLog;
import com.wanbang.console.common.Result;
import com.wanbang.console.req.InventoryLogChangeReq;
import com.wanbang.console.req.PostInboundReq;
import com.wanbang.console.req.PostTransferReq;
import com.wanbang.console.resp.InventoryLogResp;
import com.wanbang.console.service.InventoryItemService;
import com.wanbang.console.service.InventoryLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Tag(name = "订单记录相关接口")
@RequestMapping("/api/logs")
@RestController
public class LogsController {
    @Resource
    private InventoryLogService inventoryLogService;
    @Resource
    private InventoryItemService inventoryItemService;


    @Operation(summary = "查询出入转库记录")
    @GetMapping
    public Result<InventoryLogResp> logs(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "operation_type") Integer operationType,
            @RequestParam(value = "start_time", required = false) String startStr,
            @RequestParam(value = "end_time", required = false) String endStr
    ) {
        if (startStr != null) {
            startStr = startStr.substring(0, 10);
        }
        System.out.println(startStr);
        if (endStr != null) {
            endStr = endStr.substring(0, 10);
        }
        System.out.println(endStr);
        IPage<InventoryLog> resp = inventoryLogService.getLog(page, size, startStr, endStr, operationType);
        InventoryLogResp respResp = new InventoryLogResp();
        respResp.setSize(resp.getSize());
        respResp.setPage(resp.getCurrent());
        respResp.setTotal(resp.getTotal());
        respResp.setItems(resp.getRecords());
        System.out.println(respResp);
        return Result.success(respResp);
    }

    @Transactional(rollbackFor = Exception.class)  // 添加这一行
    @Operation(summary = "创建入库记录")
    @PostMapping("/inbound")
    public Result inbound(@RequestBody PostInboundReq postInboundReq) {
        if (postInboundReq.getCategory() >= 3) {
            postInboundReq.setPiecesPerBox(1);
        }
        //方法主要有两个作用 1.入库更新items 如果没有就新insert一个
        //                2.创建一个入库logs
        //postInboundReq
        //可去除operator_id 使用到items (inventory_item表)
        // 其中operatorId inventory_item_id(数据库查询出) source_warehouse(warehouseNum) quantity_change(totalPieces) remark(新增log)
        //可重复使用到logs (inventory_log表)
        System.out.println(postInboundReq);
        //先更新库存
        Integer i = inventoryItemService.postInboundItem(postInboundReq);
        System.out.println("i" + i);
        if (i <= 0) {
            return Result.fail(i);
        }
        //再根据传入的型号 找到itemId 创建Log
        Integer j = inventoryLogService.postInboundLog(postInboundReq.getOperatorId(),
                postInboundReq.getModelNumber(), postInboundReq);
        System.out.println("j" + j);
        if (j <= 0) {
            return Result.fail(j);
        }
        return Result.success();
    }

    @Transactional(rollbackFor = Exception.class)  // 添加这一行
    @Operation(summary = "创建调库记录")
    @PostMapping("/transfer")
    public Result transfer(@RequestBody PostTransferReq postTransferReq) {
        //方法主要有两个作用 1.调库更新items的信息 (改个warehouse_num)
        // (提供了inventory_item_id和quantity_change和source_warehouse和target_warehouse)
        //                2.创建一个log
        if ((postTransferReq.getSourceWarehouse() >= 6 || postTransferReq.getSourceWarehouse() <= 0)
                ||
                (postTransferReq.getTargetWarehouse() >= 6 || postTransferReq.getTargetWarehouse() <= 0)) {
            return Result.fail();
        }
        System.out.println("postTransferReq = " + postTransferReq);
        Integer i = inventoryItemService.transfer(postTransferReq.getSourceWarehouse(),
                postTransferReq.getInventoryItemId(), postTransferReq.getTargetWarehouse());
        if (i <= 0) {
            return Result.fail();
        }
        Integer j = inventoryLogService.transfer(postTransferReq);
        if (j <= 0) {
            return Result.fail();
        }
        return Result.success();
    }

    @Transactional(rollbackFor = Exception.class)
    @Operation(summary = "修改出入调库记录")
    @PutMapping()
    public Result changeLog(@RequestBody InventoryLogChangeReq req) {
        //前端的编辑后提交看似是编辑 其实是更新了两条数据 有用的只有req的Id
        Long logId = req.getId();
        //冲正 通过前端操作的id找出记录
        InventoryLog inventoryLog = inventoryLogService.getById(logId);
        //冲正log
        Integer i = inventoryLogService.itemReversal(inventoryLog);
        //冲正item
        Integer j = inventoryItemService.itemReversal(inventoryLog);
        //处理req 请求
        //调库
        if (req.getOperationType() == 3) {
            PostTransferReq transReq = new PostTransferReq();
            BeanUtils.copyProperties(req, transReq);
            this.transfer(transReq);
        }
        //入库
        if (req.getOperationType() == 1) {
            PostInboundReq postReq = new PostInboundReq();
            InventoryItem byId = inventoryItemService.getById(req.getInventoryItemId());
            BeanUtils.copyProperties(byId, postReq);
            postReq.setTotalPieces(req.getQuantityChange());
            postReq.setOperatorId(req.getOperatorId());
            this.inbound(postReq);
        }
        return Result.success();
    }

    @SaCheckRole("admin")
    @Operation(summary = "删除日志")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        boolean b = inventoryLogService.removeById(id);
        return Result.success(b);
    }
}
