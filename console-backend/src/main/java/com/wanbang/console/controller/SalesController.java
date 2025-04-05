package com.wanbang.console.controller;

import com.wanbang.console.common.Result;
import com.wanbang.console.resp.SalesTrendResp;
import com.wanbang.console.resp.TodaySaleAmountResp;
import com.wanbang.console.resp.TopSoldItemsResp;
import com.wanbang.console.resp.TotalSaleAmountResp;
import com.wanbang.console.service.OrderInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;

@Tag(name = "获取卖品相关信息接口")
@RequestMapping("/api/sales")
@RestController
@CrossOrigin
public class SalesController {
    @Resource
    private OrderInfoService orderInfoService;

    @Operation(summary = "获取最热门的五件卖品")
    @GetMapping("/top-products")
    public Result<List<TopSoldItemsResp>> TopSales() {
        List<TopSoldItemsResp> itemsResp = orderInfoService.TopSales();
        if (itemsResp != null) {
            return Result.success(itemsResp);
        }
        return Result.fail();
    }

    @Operation(summary = "获取销售趋势表单")
    @GetMapping("/trend/{year}/{month}/{length}")
    public Result<List<SalesTrendResp>> TopSalesTrend(
            @PathVariable("year") Integer year, @PathVariable("month") Integer month
            , @PathVariable("length") Integer length) {
        List<SalesTrendResp> resp = orderInfoService.TopSalesTrend(year, month, length);
        if (resp != null) {
            return Result.success(resp);
        }
        return Result.fail();
    }


    @Operation(summary = "获取今日销售额")
    @GetMapping("/today-amount")
    public Result<TodaySaleAmountResp> getTodaySaleAmount() {
        System.out.println("申请获取今日销售额");
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month += 1;
        System.out.println(month);
        String monthStr = "" + month;
        if (month < 10) {
            monthStr = "0" + month;
        }
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String dayStr = "" + day;
        if (day < 10) {
            dayStr = "0" + day;
        }
        String dateStr = year + "-" + monthStr + "-" + dayStr;
        TodaySaleAmountResp resp = orderInfoService.getTodaySales(dateStr);
        if (resp != null) {
            System.out.println("resp = " + resp);
            return Result.success(resp);
        }
        return Result.success();
    }

    @Operation(summary = "获取总销售额")
    @GetMapping("/total-amount")
    public Result<TotalSaleAmountResp> getTotalSaleAmount() {
        TotalSaleAmountResp resp = orderInfoService.getTotalSales();
        if (resp != null) {
            System.out.println("resp = " + resp);
            return Result.success(resp);
        }
        return Result.success();
    }
}
