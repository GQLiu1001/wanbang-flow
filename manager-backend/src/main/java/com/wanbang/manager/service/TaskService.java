package com.wanbang.manager.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanbang.manager.common.DeliveryOrder;
import com.wanbang.manager.common.DriverInfo;
import com.wanbang.manager.mapper.db2.DriverInfoMapper;
import jakarta.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class TaskService extends ServiceImpl<DriverInfoMapper, DriverInfo> {
    @Resource
    private DriverInfoService driverInfoService;

    @Scheduled(cron = "0 0 0 * * MON") // 每周一凌晨0点
    @Transactional(rollbackFor = Exception.class)
    public void resetAuditStatusWeekly() {
        Date now = new Date();
        System.out.println("每周一重置司机审核状态，当前时间：{}"+now);
        try {
            List<DriverInfo> driverList = driverInfoService.list();

            if (driverList.isEmpty()) {
                System.out.println("没有司机记录，无需重置审核状态");
                return;
            }

            for (DriverInfo driver : driverList) {
                driver.setAuditStatus(0);
                driver.setUpdateTime(now);
            }

            driverInfoService.updateBatchById(driverList);
            System.out.println("成功将"+driverList.size()+"个司机的审核状态重置为未审核");
        } catch (Exception e) {
            System.out.println("重置司机审核状态失败"+e);
            throw e;
        }
    }
}
