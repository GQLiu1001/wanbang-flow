package com.wanbang.console.common;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * 库存操作日志表
 * @TableName inventory_log
 */
@Data
public class  InventoryLog {
    /**
     * 日志ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 库存项ID
     */
    @JsonProperty("inventory_item_id")
    private Long inventoryItemId;

    /**
     * 操作类型（1=入库 2=出库 3=调拨 4=冲正）
     */
    @JsonProperty("operation_type")
    private Integer operationType;

    /**
     * 数量变化
     */
    @JsonProperty("quantity_change")
    private Integer quantityChange;

    /**
     * 操作人ID
     */
    @JsonProperty("operator_id")
    private Long operatorId;

    /**
     * 源仓库编码
     */
    @JsonProperty("source_warehouse")
    private Integer sourceWarehouse;

    /**
     * 目标仓库编码
     */
    @JsonProperty("target_warehouse")
    private Integer targetWarehouse;

    /**
     * 操作备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonProperty("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonProperty("update_time")
    private Date updateTime;


}