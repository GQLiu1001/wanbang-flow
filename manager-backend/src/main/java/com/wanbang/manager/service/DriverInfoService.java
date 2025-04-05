package com.wanbang.manager.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wanbang.manager.common.DriverInfo;

/**
* @author 11965
* @description 针对表【driver_info(司机信息表)】的数据库操作Service
* @createDate 2025-04-02 20:51:58
*/
public interface DriverInfoService extends IService<DriverInfo> {

    IPage<DriverInfo> getDriverList(int page, int size, String driverName, String driverPhone, String auditStatus, String workStatus);



    void resetDriverMoney(Long id);

    void updateAudit(Long id, Integer auditStatus, String auditRemark, String auditor);
}
