package com.wanbang.driver.resp;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RouteResp {
    private Integer distance; // 距离（单位：米）
    private Integer duration; // 耗时（单位：秒）
    private List<List<BigDecimal>> polyline; // 第一条路线的完整路径点坐标列表（[[经度, 纬度], ...]）
}