package com.wanbang.console.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanbang.console.common.*;
import com.wanbang.console.dto.LoginDTO;
import com.wanbang.console.enums.ResultCode;
import com.wanbang.console.exception.WanbangException;
import com.wanbang.console.mapper.SysRoleMapper;
import com.wanbang.console.mapper.SysUserMapper;
import com.wanbang.console.mapper.SysUserRoleMapper;
import com.wanbang.console.req.UserInfoChangeReq;
import com.wanbang.console.resp.LoginResp;
import com.wanbang.console.service.SysUserService;
import com.wanbang.console.vo.UserInfoVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @author 11965
* @description 针对表【sys_user(系统用户表)】的数据库操作Service实现
* @createDate 2025-02-24 15:24:52
*/
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
    implements SysUserService{
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysUserRoleMapper sysUserRoleMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;

    @Override
    public LoginResp login(String username, String password) {
        LoginDTO loginDTO = sysUserMapper.login(username,password);
        Integer roleId = loginDTO.getRoleId();
        System.out.println("从数据库查出的loginDTO"+loginDTO);
        LoginResp loginResp = new LoginResp();
        BeanUtils.copyProperties(loginDTO,loginResp);
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getId, roleId);
        SysRole sysRole = sysRoleMapper.selectOne(queryWrapper);
        loginResp.setRoleKey(sysRole.getRoleKey());
        System.out.println("返回给前端的loginResp"+loginResp);
        return loginResp;
    }

    @Override
    public Integer deleteUser(Long id) {
        int i = sysUserMapper.deleteById(id);
        System.out.println("sysUserMapper的删除结果"+i);
        return i;
    }

    @Override
    public Integer registry(String username, String password, String phone) {
        SysUser sysUser = new SysUser();
        sysUser.setUsername(username);
        sysUser.setPassword(password);
        sysUser.setPhone(phone);
        sysUser.setCreateTime(new Date());
        sysUser.setUpdateTime(new Date());
        int i = sysUserMapper.insert(sysUser);
        if (i <= 0){
            return 0;
        }
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setUserId(sysUser.getId());
        sysUserRole.setRoleId(2L);
        int j = sysUserRoleMapper.insert(sysUserRole);
        System.out.println("sysUserRole表更新了= " + j +"行");
        return i + j;
    }

    @Override
    public Integer changePassword(String username, String phone, String password) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, username);
        queryWrapper.eq(SysUser::getPhone,phone);
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper);
        if (sysUser == null) {
            throw new WanbangException(ResultCode.FAIL);
        }
        System.out.println(sysUser.getPassword());
        System.out.println(password);
        sysUser.setPassword(password);
        int i = sysUserMapper.updateById(sysUser);
        return i;
    }

    @Override
    public IPage<UserInfoVO> getUserList(int page, int size) {
        IPage<UserInfoVO> pageParam = new Page<>(page, size);
        IPage<UserInfoVO> result = sysUserMapper.selectUserListWithRole(pageParam);
        System.out.println(result);
        return result;
    }

    @Override
    public Integer updateUser(Long id, UserInfoChangeReq userInfoChangeReq) {
        System.out.println("userInfoChangeReq = " + userInfoChangeReq);
        SysUser sysUser = sysUserMapper.selectById(id);
        String originPwd = sysUser.getPassword();
        System.out.println("originPwd = " + originPwd);
        String oldPwd4Check = SaSecureUtil.md5(userInfoChangeReq.getOldPassword());
        System.out.println("oldPwd4Check = " + oldPwd4Check);
        String newPwd = SaSecureUtil.md5(userInfoChangeReq.getPassword());
        System.out.println("newPwd = " + newPwd);
        if (userInfoChangeReq.getPassword() != null){
            if (originPwd.equals(newPwd)) {
                throw new WanbangException(ResultCode.THESAMEPSWERROR);
            }
            if (!originPwd.equals(oldPwd4Check)){
                throw new WanbangException(ResultCode.FAIL);
            }
        }
        SysUser newSysUser = new SysUser();
        newSysUser.setId(id);
        newSysUser.setUsername(userInfoChangeReq.getUsername());
        newSysUser.setPhone(userInfoChangeReq.getPhone());
        newSysUser.setPassword(userInfoChangeReq.getPassword());
        newSysUser.setUpdateTime(new Date());
        int i = sysUserMapper.updateById(newSysUser);
        return i;
    }

    @Override
    public Integer updateUserAvatar(Integer id, String url) {
        Integer i = sysUserMapper.updateUserAvatar(id,url);
        return i;
    }


}




