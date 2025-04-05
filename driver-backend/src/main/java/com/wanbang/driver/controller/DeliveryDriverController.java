package com.wanbang.driver.controller;


import com.wanbang.driver.common.DriverInfo;
import com.wanbang.driver.common.Result;
import com.wanbang.driver.resp.DriverInfoResp;
import com.wanbang.driver.service.DriverInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Tag(name = "司机信息相关")
@RestController
@RequestMapping("/api/driver")
public class DeliveryDriverController {
    @Resource
    private DriverInfoService driverInfoService;

    @Operation(summary = "获取司机信息")
    @GetMapping("/info/{id}")
    public Result<DriverInfoResp> driverInfo(@PathVariable("id") Long id) {
        DriverInfo byId = driverInfoService.getById(id);
        DriverInfoResp driverInfoResp = new DriverInfoResp();
        BeanUtils.copyProperties(byId, driverInfoResp);
        return Result.success(driverInfoResp);
    }

    @Operation(summary = "更新司机信息")
    @PostMapping("/update-info/{id}")
    public Result updateDriverInfo(@RequestParam(value = "name",required = false) String name, @PathVariable String id) {
        driverInfoService.changeInfo(name,id);
        return Result.success();
    }

    @Operation(summary = "更新司机状态")
    @PostMapping("/status/{id}")
    public Result updateDriverStatus(@PathVariable("id") Long id,
                                   @RequestParam(value = "workStatus",required = false) Integer workStatus) {

        driverInfoService.updateDriverStatus(id,workStatus);
        return Result.success();
    }

    @Operation(summary = "获取钱包信息")
    @GetMapping("/wallet/{id}")
    public Result<BigDecimal> wallet(@PathVariable("id") Long id) {
        return  Result.success(driverInfoService.getById(id).getMoney());
    }

    @PostMapping("/location/{id}")
    public Result updateLocation(@PathVariable Integer id,
                                 @RequestParam("latitude")BigDecimal latitude,
                                 @RequestParam("longitude")BigDecimal longitude
    ) {
        driverInfoService.updateLocation(id,latitude,longitude);
        return Result.success();
    }
}
