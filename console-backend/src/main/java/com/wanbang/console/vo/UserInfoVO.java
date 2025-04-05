package com.wanbang.console.vo;

import lombok.Data;

@Data
public class UserInfoVO {
    private int id;
    private String username;
    private String phone;
    private String roleKey;
    private String description;
}
