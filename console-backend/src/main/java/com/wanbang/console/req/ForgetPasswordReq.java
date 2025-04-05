package com.wanbang.console.req;

import lombok.Data;
//忘记密码Req
@Data
public class ForgetPasswordReq {
    private String username;
    private String phone;
    private String newPassword;
}
