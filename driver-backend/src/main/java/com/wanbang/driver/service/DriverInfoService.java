package com.wanbang.driver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wanbang.driver.common.DriverInfo;
import com.wanbang.driver.resp.LoginInfoResp;
import me.chanjar.weixin.common.error.WxErrorException;

import java.math.BigDecimal;

/**
* @author 11965
* @description 针对表【driver_info(司机信息表)】的数据库操作Service
* @createDate 2025-04-02 20:51:11
*/
public interface DriverInfoService extends IService<DriverInfo> {

    LoginInfoResp login(String code, String phone) throws WxErrorException;

    void changeInfo(String driverName, String userId);

    void logout(long id);

    void updateDriverStatus(Long id, Integer workStatus);

    void updateLocation(Integer id, BigDecimal latitude, BigDecimal longitude);
}
