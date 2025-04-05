package com.wanbang.manager.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanbang.manager.common.DriverAuditLog;
import com.wanbang.manager.common.DriverInfo;
import com.wanbang.manager.mapper.db2.DriverAuditLogMapper;
import com.wanbang.manager.mapper.db2.DriverInfoMapper;
import com.wanbang.manager.service.DriverInfoService;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @author 11965
* @description 针对表【driver_info(司机信息表)】的数据库操作Service实现
* @createDate 2025-04-02 20:51:58
*/
@Service
public class DriverInfoServiceImpl extends ServiceImpl<DriverInfoMapper, DriverInfo>
    implements DriverInfoService{
    @Resource
    private DriverInfoMapper driverInfoMapper;
    @Resource
    private DriverAuditLogMapper driverAuditLogMapper;
    @Override
    public IPage<DriverInfo> getDriverList(int page, int size, String driverName, String driverPhone, String auditStatus, String workStatus) {
        IPage<DriverInfo> pageInfo = new Page<>(page, size);
        return driverInfoMapper.getDriverList(pageInfo,driverName,driverPhone,auditStatus,workStatus);
    }
    @GlobalTransactional(rollbackFor = Exception.class)
    @Override
    public void updateAudit(Long id, Integer auditStatus, String auditRemark, String auditor) {
        DriverInfo driverInfo = driverInfoMapper.selectById(id);
        driverInfo.setAuditStatus(auditStatus);
        driverInfoMapper.updateById(driverInfo);
        DriverAuditLog driverAuditLog = new DriverAuditLog();
        driverAuditLog.setDriverId(id);
        driverAuditLog.setAuditStatus(auditStatus);
        driverAuditLog.setAuditRemark(auditRemark);
        driverAuditLog.setAuditor(auditor);
        driverAuditLog.setCreateTime(new Date());
        driverAuditLog.setUpdateTime(new Date());
        driverAuditLogMapper.insert(driverAuditLog);


    }

    @Override
    public void resetDriverMoney(Long id) {
        DriverInfo driverInfo = driverInfoMapper.selectById(id);
        driverInfo.setMoney(0);
        driverInfoMapper.updateById(driverInfo);
    }

}




