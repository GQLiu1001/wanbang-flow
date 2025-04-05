package com.wanbang.driver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanbang.driver.common.DriverAuditLog;
import com.wanbang.driver.mapper.DriverAuditLogMapper;
import com.wanbang.driver.service.DriverAuditLogService;
import org.springframework.stereotype.Service;

/**
* @author 11965
* @description 针对表【driver_audit_log(司机审核记录表)】的数据库操作Service实现
* @createDate 2025-04-02 20:51:11
*/
@Service
public class DriverAuditLogServiceImpl extends ServiceImpl<DriverAuditLogMapper, DriverAuditLog>
    implements DriverAuditLogService{

}




