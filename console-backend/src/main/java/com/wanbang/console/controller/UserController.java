package com.wanbang.console.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wanbang.console.common.Result;
import com.wanbang.console.req.UserInfoChangeReq;
import com.wanbang.console.resp.UserListResp;
import com.wanbang.console.service.SysUserService;
import com.wanbang.console.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户信息接口", description = "用户信息相关接口")
@RequestMapping("/api/users")
@CrossOrigin
@RestController
public class UserController {
    @Resource
    private SysUserService sysUserService;

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result deleteUser(@PathVariable Long id) {
        int loginIdAsInt = StpUtil.getLoginIdAsInt();
        List<String> roleList = StpUtil.getRoleList(loginIdAsInt);
        System.out.println(roleList);
        StpUtil.checkRole("admin");
        Integer i = sysUserService.deleteUser(id);
        if (i > 0) {
            return Result.success();
        }
        return Result.fail();
    }

    @Operation(summary = "获取用户列表")
    @GetMapping
    public Result<UserListResp> getUsers(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        IPage<UserInfoVO> result = sysUserService.getUserList(page, size);
        // 构造响应对象
        UserListResp resp = new UserListResp();
        resp.setItems(result.getRecords());         // 当前页数据列表
        resp.setTotal(result.getTotal());          // 总记录数
        resp.setPage(result.getCurrent());         // 当前页码
        resp.setSize(result.getSize());           // 每页大小
        return Result.success(resp);
    }

    @Operation(summary = "修改用户信息")
    @PutMapping("/{id}")
    public Result updateUser(@PathVariable Long id, @RequestBody UserInfoChangeReq userInfoChangeReq) {
        Integer i = sysUserService.updateUser(id, userInfoChangeReq);
        if (i > 0) {
            return Result.success();
        }
        return Result.fail();
    }
}
