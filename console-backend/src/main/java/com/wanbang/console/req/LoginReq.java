package com.wanbang.console.req;

import lombok.Data;
//用户登录req
@Data
public class LoginReq {
    private String username;
    private String password;
}
