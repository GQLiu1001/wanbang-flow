package com.wanbang.console.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wanbang.console.common.InventoryItem;
import com.wanbang.console.common.Result;
import com.wanbang.console.req.InventoryItemsChangeReq;
import com.wanbang.console.resp.InventoryItemsResp;
import com.wanbang.console.resp.ItemModelResp;
import com.wanbang.console.service.InventoryItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Tag(name = "库存相关接口")
@RequestMapping("/api/inventory")
@RestController
public class ItemsController {
    @Resource
    private InventoryItemService inventoryItemService;

    @Operation(summary = "查询库存列表")
    @GetMapping("/items")
    public Result<InventoryItemsResp> getItems(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "category", required = false) Integer category,
            @RequestParam(value = "surface", required = false) Integer surface
    ) {
        IPage<InventoryItem> resp = inventoryItemService.getItems(page, size, category, surface);
        System.out.println(resp);
        InventoryItemsResp inventoryItemsResp = new InventoryItemsResp();
        inventoryItemsResp.setTotal(resp.getTotal());
        inventoryItemsResp.setSize(resp.getSize());
        inventoryItemsResp.setPage(resp.getCurrent());
        inventoryItemsResp.setItems(resp.getRecords());
        System.out.println(inventoryItemsResp);
        return Result.success(inventoryItemsResp);
    }

    @Operation(summary = "修改库存")
    @PutMapping("/items/{id}")
    public Result changeItem(@PathVariable("id") Integer id, @RequestBody InventoryItemsChangeReq req) {
        System.out.println(req);
        Integer i = inventoryItemService.changeItems(id, req);
        if (i > 0) {
            return Result.success();
        }
        return Result.fail();
    }

    @SaCheckRole("admin")
    @Operation(summary = "删除库存")
    @DeleteMapping("/items/{id}")
    public Result deleteItem(@PathVariable("id") Integer id) {
        Integer i = inventoryItemService.deleteById(id);
        if (i > 0) {
            return Result.success();
        }
        return Result.fail();
    }

    @Operation(summary = "自动回填id和数量")
    @GetMapping("/items/model/{modelNumber}")
    public Result<ItemModelResp> getItemModel(@PathVariable("modelNumber") String modelNumber) {
        ItemModelResp resp = inventoryItemService.getItemModel(modelNumber);
        if (resp != null) {
            return Result.success(resp);
        }
        return Result.fail();
    }

}
