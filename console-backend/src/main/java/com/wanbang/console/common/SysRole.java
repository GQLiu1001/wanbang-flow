package com.wanbang.console.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * 角色表
 * @TableName sys_role
 */
@Data
public class SysRole {
    /**
     * 角色ID
     */
    private Long id;

    /**
     * 角色名称
     */
    @JsonProperty("role_name")
    private String roleName;

    /**
     * 角色标识
     */
    @JsonProperty("role_key")
    private String roleKey;

    /**
     * 角色描述
     */

    private String description;

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