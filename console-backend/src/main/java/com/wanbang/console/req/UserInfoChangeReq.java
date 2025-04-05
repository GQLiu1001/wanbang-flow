package com.wanbang.console.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
//修改用户信息
@Data
public class UserInfoChangeReq {
    private String username;
    private String password;
    @JsonProperty("oldPassword")
    private String oldPassword;
    private String phone;
    private String avatar;
}
