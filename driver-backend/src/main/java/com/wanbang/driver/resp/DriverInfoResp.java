package com.wanbang.driver.resp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class DriverInfoResp {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String phone;

    private String avatar;

    private Integer auditStatus;

    private Integer workStatus;
}
