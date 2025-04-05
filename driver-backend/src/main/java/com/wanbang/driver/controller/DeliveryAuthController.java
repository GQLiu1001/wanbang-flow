package com.wanbang.driver.controller;


import com.wanbang.driver.common.DriverInfo;
import com.wanbang.driver.common.Result;
import com.wanbang.driver.resp.LoginInfoResp;
import com.wanbang.driver.service.DriverInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.web.bind.annotation.*;

@Tag(name = "派送系统司机认证相关接口")
@RestController
@RequestMapping("/api/driver/auth")
public class DeliveryAuthController {
    @Resource
    private DriverInfoService driverInfoService;
    @Operation(summary = "司机登陆")
    @PostMapping("/login")
    public Result<LoginInfoResp> login(@RequestParam String code, @RequestParam String phone) throws WxErrorException {
        LoginInfoResp resp = driverInfoService.login(code,phone);
        return Result.success(resp);
    }
    
    @Operation(summary = "司机登出")
    @PostMapping("/logout/{id}")
    public Result logout(@PathVariable String id){
        driverInfoService.logout(Long.parseLong(id));
        return Result.success();

    }

    @Operation(summary = "获取审核状态")
    @GetMapping("/audit-status/{id}")
    public Result<Integer> checkStatus(@PathVariable String id){
        DriverInfo byId = driverInfoService.getById(Long.parseLong(id));
        return Result.success(byId.getAuditStatus());
    }
}
