package com.wanbang.console.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wanbang.console.common.InventoryLog;

/**
* @author 11965
* @description 针对表【inventory_log(库存操作日志表)】的数据库操作Mapper
* @createDate 2025-02-24 15:24:52
* @Entity com.wanbang.common.InventoryLog
*/
public interface InventoryLogMapper extends BaseMapper<InventoryLog> {

    IPage<InventoryLog> getLog(IPage<InventoryLog> pageParam, String startStr, String endStr, Integer operationType);


}




