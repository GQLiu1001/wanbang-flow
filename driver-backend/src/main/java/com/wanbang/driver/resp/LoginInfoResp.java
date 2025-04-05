package com.wanbang.driver.resp;

import lombok.Data;

@Data
public class LoginInfoResp {
    private String token;
    private Long id;
    private String name;
    private String phone;
    private String avatar;
    private Integer auditStatus;
    private Integer workStatus;
}
