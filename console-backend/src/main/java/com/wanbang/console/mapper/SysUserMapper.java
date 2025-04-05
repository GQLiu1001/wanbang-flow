package com.wanbang.console.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wanbang.console.common.SysUser;
import com.wanbang.console.dto.LoginDTO;
import com.wanbang.console.vo.UserInfoVO;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 11965
* @description 针对表【sys_user(系统用户表)】的数据库操作Mapper
* @createDate 2025-02-24 15:24:52
* @Entity com.wanbang.common.SysUser
*/
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    LoginDTO login(String username, String password);


    IPage<UserInfoVO> selectUserListWithRole(IPage<UserInfoVO> page);


    Integer updateUserAvatar(Integer id, String url);
}




