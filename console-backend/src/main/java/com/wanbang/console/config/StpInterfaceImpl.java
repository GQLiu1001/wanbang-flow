package com.wanbang.console.config;

import cn.dev33.satoken.stp.StpInterface;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wanbang.console.common.SysRole;
import com.wanbang.console.common.SysUserRole;
import com.wanbang.console.mapper.SysRoleMapper;
import com.wanbang.console.mapper.SysUserRoleMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StpInterfaceImpl  implements StpInterface {
    @Resource
    private SysUserRoleMapper sysUserRoleMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;
    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 暂时不使用权限码，返回空列表
        // 如果未来需要权限码控制，可以在这里查询并返回
        return new ArrayList<>();
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> list = new ArrayList<String>();
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, loginId);
        SysUserRole sysUserRole = sysUserRoleMapper.selectOne(wrapper);
        Long roleId = sysUserRole.getRoleId();
        LambdaQueryWrapper<SysRole> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(SysRole::getId, roleId);
        SysRole sysRole = sysRoleMapper.selectOne(wrapper1);
        list.add(sysRole.getRoleKey());
        return list;
    }
}
