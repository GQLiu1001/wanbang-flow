package com.wanbang.manager.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wanbang.manager.common.DriverInfo;
import com.wanbang.manager.common.Result;
import com.wanbang.manager.common.SysUserRole;
import com.wanbang.manager.resp.DriverListResp;
import com.wanbang.manager.service.DriverInfoService;
import com.wanbang.manager.service.SysUserRoleService;
import com.wanbang.manager.util.UserContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台司机相关接口")
@RequestMapping("/api/delivery/driver")
@RestController
public class MgrDriverController {
    @Resource
    private DriverInfoService driverInfoService;
    @Resource
    private SysUserRoleService sysUserRoleService;
    @Operation(summary = "获取司机列表")
    @GetMapping
    public Result<DriverListResp> getDriverList(@RequestParam(value = "page",defaultValue = "1") int page ,
                                                @RequestParam(value = "size",defaultValue = "10") int size ,
                                                @RequestParam(value = "name",required = false) String driverName,
                                                @RequestParam(value = "phone",required = false) String driverPhone,
                                                @RequestParam(value = "auditStatus",required = false) String auditStatus,
                                                @RequestParam(value = "workStatus",required = false) String workStatus
    ) {
        System.out.println("触发获取司机列表");
        IPage<DriverInfo> resp = driverInfoService.getDriverList(page,size,driverName,driverPhone,auditStatus,workStatus);
        DriverListResp driverListResp = new DriverListResp();
        driverListResp.setTotal(resp.getTotal());
        driverListResp.setRecords(resp.getRecords());
        return Result.success(driverListResp);

    }

    @Operation(summary = "审核司机资格")
    @PutMapping("/{id}/approval")
    public Result approvalAudit(@PathVariable Long id,
                              @RequestParam("auditStatus") Integer auditStatus,
                              @RequestParam(value = "auditRemark",required = false) String auditRemark,
                              @RequestParam("auditor") String auditor){
        boolean userRole = getUserRole();
        if (!userRole) {
            return Result.fail();
        }
        driverInfoService.updateAudit(id,auditStatus,auditRemark,auditor);
        return Result.success();

    }
    @Operation(summary = "拒绝司机资格")
    @PutMapping("/{id}/rejection")
    public Result rejectAudit(@PathVariable Long id,
                               @RequestParam("auditStatus") Integer auditStatus,
                               @RequestParam(value = "auditRemark",required = false) String auditRemark,
                               @RequestParam("auditor") String auditor){
        boolean userRole = getUserRole();
        if (!userRole) {
            return Result.fail();
        }
        driverInfoService.updateAudit(id,auditStatus,auditRemark,auditor);
        return Result.success();

    }

    @Operation(summary = "删除司机")
    @DeleteMapping("/{id}/delete")
    public Result deleteDriver(@PathVariable Long id ,@RequestParam(value = "auditor",required = false) String auditor){
        boolean userRole = getUserRole();
        System.out.println(userRole);
        if (userRole == false){
            return Result.fail("无权限");
        }
        driverInfoService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "清零司机")
    @DeleteMapping("/{id}/reset-money")
    public Result resetDriverMoney(@PathVariable Long id ,@RequestParam("auditor") String auditor){
        boolean userRole = getUserRole();
        if (!userRole) {
            return Result.fail();
        }
        driverInfoService.resetDriverMoney(id);
        return Result.success();
    }

    public boolean getUserRole(){
        String userId = UserContextHolder.getUserId();
        SysUserRole byId = sysUserRoleService.getById(Long.valueOf(userId));
        return byId.getRoleId() == 1;
//        return true;
    }
}

