package com.wanbang.console.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 用户角色关联表
 * @TableName sys_user_role
 */
@Data
public class SysUserRole {
    /**
     * 用户ID
     */
    @JsonProperty("user_id")
    private Long userId;

    /**
     * 角色ID
     */
    @JsonProperty("role_id")
    private Long roleId;



}