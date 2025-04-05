package com.wanbang.console.resp;

import lombok.Data;
//登录resp
@Data
public class LoginResp {
    private Integer id;
    private String username;
    private String avatar;
    private String phone;
    private String roleKey;
}
