package com.wanbang.console.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanbang.console.common.InventoryItem;
import com.wanbang.console.common.InventoryLog;
import com.wanbang.console.common.ItemAftersaleChange;
import com.wanbang.console.common.OrderItem;
import com.wanbang.console.enums.ResultCode;
import com.wanbang.console.exception.WanbangException;
import com.wanbang.console.mapper.InventoryItemMapper;
import com.wanbang.console.mapper.InventoryLogMapper;
import com.wanbang.console.mapper.OrderItemMapper;
import com.wanbang.console.req.OrderItemPostReq;
import com.wanbang.console.req.PostInboundReq;
import com.wanbang.console.req.PostTransferReq;
import com.wanbang.console.service.InventoryLogService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author 11965
* @description 针对表【inventory_log(库存操作日志表)】的数据库操作Service实现
* @createDate 2025-02-24 15:24:52
*/
@Service
public class InventoryLogServiceImpl extends ServiceImpl<InventoryLogMapper, InventoryLog>
    implements InventoryLogService{
    @Resource
    private InventoryLogMapper inventoryLogMapper;
    @Resource
    private InventoryItemMapper inventoryItemMapper;
    @Resource
    private OrderItemMapper orderItemMapper;

    @Override
    public IPage<InventoryLog> getLog(Integer page, Integer size, String startStr, String endStr, Integer operationType) {
        IPage<InventoryLog> iPage = new Page<>(page, size);
        IPage<InventoryLog> pages = inventoryLogMapper.getLog(iPage,startStr,endStr,operationType);
        System.out.println(pages);
        return pages;
    }

    @Override
    public Integer itemReversal(InventoryLog inventoryLog) {
        // 冲正记录
        System.out.println("需要冲正的记录"+inventoryLog);
        Long inventoryItemId = inventoryLog.getInventoryItemId();
        Integer quantityChange =inventoryLog.getQuantityChange();
        Long operatorId = inventoryLog.getOperatorId();
        Integer sourceWarehouse =inventoryLog.getSourceWarehouse();
        Integer targetWarehouse =inventoryLog.getTargetWarehouse();
        Integer operationType = inventoryLog.getOperationType();

        InventoryLog inventoryLog1 = new InventoryLog();
        inventoryLog1.setInventoryItemId(inventoryItemId);
        inventoryLog1.setOperatorId(operatorId);
        //如果是调库 源库目标库调换 数量不变
        if (operationType == 3){
            inventoryLog1.setQuantityChange(quantityChange);
            inventoryLog1.setSourceWarehouse(targetWarehouse);
            inventoryLog1.setTargetWarehouse(sourceWarehouse);
        }
        //如果是出库 入库 不用改变仓库 数量相反
        inventoryLog1.setQuantityChange(-quantityChange);
        inventoryLog1.setTargetWarehouse(targetWarehouse);
        inventoryLog1.setSourceWarehouse(sourceWarehouse);
        inventoryLog1.setRemark("冲正记录");
        inventoryLog1.setCreateTime(new Date());
        inventoryLog1.setUpdateTime(new Date());
        inventoryLog1.setOperationType(4);
        System.out.println("inventoryLog1 = " + inventoryLog1);
        int insert = inventoryLogMapper.insert(inventoryLog1);
        System.out.println("insert = " + insert);
        return insert;
    }

    @Override
    public Integer postInboundLog(Long operatorId, String modelNumber , PostInboundReq postInboundReq) {
        InventoryLog inventoryLog = new InventoryLog();
        Long inventoryItemId = inventoryItemMapper.findInventoryItemId(modelNumber);
        if (inventoryItemId == null) {
            throw new WanbangException(ResultCode.FAIL);
        }
        System.out.println("inventoryItemId"+inventoryItemId);
        inventoryLog.setInventoryItemId(inventoryItemId);
        inventoryLog.setOperationType(1);
        inventoryLog.setQuantityChange(postInboundReq.getTotalPieces());
        inventoryLog.setOperatorId(operatorId);
        inventoryLog.setTargetWarehouse(postInboundReq.getWarehouseNum());
        inventoryLog.setRemark("入库"+postInboundReq.getModelNumber());
        inventoryLog.setUpdateTime(new Date());
        inventoryLog.setCreateTime(new Date());
        System.out.println("inventoryLog = " + inventoryLog);
        Integer j = inventoryLogMapper.insert(inventoryLog);
        System.out.println("j"+j);
        return j;
    }

    @Override
    public Integer transfer(PostTransferReq postTransferReq) {
        LambdaQueryWrapper<InventoryItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryItem::getId, postTransferReq.getInventoryItemId());
        InventoryItem inventoryItem = inventoryItemMapper.selectOne(wrapper);

        InventoryLog inventoryLog = new InventoryLog();
        BeanUtils.copyProperties(postTransferReq, inventoryLog);
        inventoryLog.setUpdateTime(new Date());
        inventoryLog.setCreateTime(new Date());
        inventoryLog.setQuantityChange(inventoryItem.getTotalPieces());
        inventoryLog.setOperationType(3);
        if (postTransferReq.getRemark() == null || postTransferReq.getRemark().isEmpty()) {
            inventoryLog.setRemark("调库物品id"+inventoryItem.getId());
        }
        System.out.println("inventoryLog = " + inventoryLog);
        Integer j = inventoryLogMapper.insert(inventoryLog);
        System.out.println("j"+j);
        return j;
    }

    @Override
    public Integer outbound(List<OrderItemPostReq> items) {
        items.forEach(item -> {
            Long itemId = item.getItemId();
            Integer quantity = item.getQuantity();
            Integer sourceWarehouse = item.getSourceWarehouse();
            Long loginId = StpUtil.getLoginIdAsLong();
            InventoryLog inventoryLog = new InventoryLog();
            inventoryLog.setInventoryItemId(itemId);
            inventoryLog.setOperationType(2);
            inventoryLog.setQuantityChange(quantity);
            inventoryLog.setOperatorId(loginId);
            inventoryLog.setSourceWarehouse(sourceWarehouse);
            inventoryLog.setUpdateTime(new Date());
            inventoryLog.setCreateTime(new Date());
            inventoryLog.setRemark("出库");
            int i = inventoryLogMapper.insert(inventoryLog);
            System.out.println("出库log更新 = " + i);
        });
        return 1;
    }

    @Override
    public Integer aftersale(ItemAftersaleChange item) {
        System.out.println("售后inventoryLog的item = " + item);
        OrderItem item1 = orderItemMapper.selectById(item.getOrderItemId());
        InventoryItem inventoryItem = inventoryItemMapper.selectById(item1.getItemId());
        if (item.getQuantityChange()<0){
            //多退  订单的item数量减少 库存item增加
            InventoryLog inventoryLog = new InventoryLog();
            inventoryLog.setInventoryItemId(item1.getItemId());
            inventoryLog.setUpdateTime(new Date());
            inventoryLog.setCreateTime(new Date());
            inventoryLog.setOperationType(1);
            inventoryLog.setQuantityChange(-item.getQuantityChange());
            inventoryLog.setRemark("售后 多退 订单的item数量减少 库存item增加");
            inventoryLog.setOperatorId(StpUtil.getLoginIdAsLong());
            inventoryLog.setSourceWarehouse(inventoryItem.getWarehouseNum());
            int i = inventoryLogMapper.insert(inventoryLog);
            System.out.println("i = " + i);
            return i;
        }else {
            //少补
            InventoryLog inventoryLog = new InventoryLog();
            inventoryLog.setInventoryItemId(item1.getItemId());
            inventoryLog.setUpdateTime(new Date());
            inventoryLog.setCreateTime(new Date());
            inventoryLog.setOperationType(1);
            inventoryLog.setQuantityChange(-item.getQuantityChange());
            inventoryLog.setRemark("售后 少补 订单的item数量增加 库存item减少");
            inventoryLog.setOperatorId(StpUtil.getLoginIdAsLong());
            inventoryLog.setSourceWarehouse(inventoryItem.getWarehouseNum());
            int i = inventoryLogMapper.insert(inventoryLog);
            System.out.println("i = " + i);
            return i;
        }
    }


}




