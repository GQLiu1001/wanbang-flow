package com.wanbang.console.req;

import lombok.Data;
//用户注册req
@Data
public class RegisterReq {
    private String username;
    private String password;
    private String phone;
}
