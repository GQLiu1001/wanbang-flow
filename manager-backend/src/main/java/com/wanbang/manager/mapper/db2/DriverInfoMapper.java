package com.wanbang.manager.mapper.db2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wanbang.manager.common.DriverInfo;

/**
* @author 11965
* @description 针对表【driver_info(司机信息表)】的数据库操作Mapper
* @createDate 2025-04-02 20:51:58
* @Entity com.wanbang.manager.common.DriverInfo
*/
public interface DriverInfoMapper extends BaseMapper<DriverInfo> {

    IPage<DriverInfo> getDriverList(IPage<DriverInfo> pageInfo, String driverName, String driverPhone, String auditStatus, String workStatus);

}




