package com.wanbang.manager.resp;


import com.wanbang.manager.common.DriverInfo;
import lombok.Data;

import java.util.List;
@Data
public class DriverListResp {
    private Long total;
    private List<DriverInfo> records;
}
