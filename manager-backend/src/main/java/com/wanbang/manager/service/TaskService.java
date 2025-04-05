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
    /*
    * Cron 表达式基本语法
    * Spring 的 Cron 表达式由 6个字段 组成（从左到右），用空格分隔，表示时间的各个维度：
    * 秒 分 时 日(月) 月 日(周)   ！！日(月)和日(周) 冲突
    * 秒 (Seconds)：0-59
    * 分 (Minutes)：0-59
    * 时 (Hours)：0-23
    * 日(月) (Day of Month)：1-31
    * 月 (Month)：1-12 或 JAN-DEC（月份缩写）
    * 日(周) (Day of Week)：1-7（1=星期日，7=星期六）或 SUN-SAT（星期缩写）
    *
    * *（星号）：
    * 表示该字段的所有可能值。
    * 示例：* * * * * * 表示每秒执行。
    *
    * ,（逗号）：
    * 指定多个值。
    * 示例：0 0 12,18 * * * 表示每天12点和18点执行。
    *
    * -（连字符）：
    * 指定范围。
    * 示例：0 0 9-17 * * * 表示每天9点到17点每小时执行。
    *
    * /（斜杠）：
    * 表示增量或步长。
    * 示例：0 0/15 * * * * 表示每15分钟执行一次（0、15、30、45分）。
    *
    * ?（问号）：
    * 用于“日(月)”和“日(周)”字段，表示无具体值，避免冲突。
    * 示例：0 0 0 1 * ? 表示每月1号执行（周字段用?避免干扰）。
    *
    * L（Last）：
    * 表示“最后”，用于“日(月)”或“日(周)”。
    * 示例：0 0 0 L * ? 表示每月最后一天执行。
    *
    * W（Weekday）：
    * 表示最近的工作日（周一到周五），用于“日(月)”字段。
    * 示例：0 0 0 15W * ? 表示每月15号最近的工作日执行。
    *
    * #（井号）：
    * 用于“日(周)”字段，表示第几周的星期几。
    * 示例：0 0 0 * * 2#1 表示每月第一个星期一执行。
    * # 前面的值是星期几（例如 2 表示星期一，MON 也可以）。
    * # 后面的值是第几周（例如 1 表示第1周，2 表示第2周）。
    *
    * 0 0 9 * * MON-FRI
    * 每周一到周五 上午9点执行
    *
    * 0 0 9 * 2 MON-FRI
    * 每年 2月的周一到周五 上午9点执行
    * */
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
