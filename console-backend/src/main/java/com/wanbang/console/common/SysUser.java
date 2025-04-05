package com.wanbang.console.common;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * 系统用户表
 * @TableName sys_user
 */
@Data
public class SysUser {
    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 登录账号
     */
    private String username;

    /**
     * 加密密码
     */
    private String password;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 删除标记
     */
    @JsonProperty("is_deleted")
    private Integer isDeleted;

    /**
     *
     */
    @JsonProperty("create_time")
    private Date createTime;

    /**
     *
     */
    @JsonProperty("update_time")
    private Date updateTime;


}