package com.ski.resort.controller;

import com.ski.resort.common.Result;
import com.ski.resort.service.DashboardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 数据统计Dashboard Controller
 */
@Api(tags = "数据统计分析")
@RestController
@RequestMapping("/api/v1/dashboard")
@CrossOrigin
public class DashboardController {
    
    @Autowired
    private DashboardService dashboardService;
    
    @ApiOperation("获取统计概览数据")
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> overview = dashboardService.getOverview();
        return Result.success(overview);
    }
    
    @ApiOperation("获取收入统计数据")
    @GetMapping("/revenue")
    public Result<Map<String, Object>> getRevenueStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Map<String, Object> revenueStats = dashboardService.getRevenueStats(startDate, endDate);
        return Result.success(revenueStats);
    }
    
    @ApiOperation("获取订单统计数据")
    @GetMapping("/orders")
    public Result<Map<String, Object>> getOrderStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Map<String, Object> orderStats = dashboardService.getOrderStats(startDate, endDate);
        return Result.success(orderStats);
    }
    
    @ApiOperation("获取热门雪具排行")
    @GetMapping("/popular-equipment")
    public Result<Map<String, Object>> getPopularEquipment() {
        Map<String, Object> data = dashboardService.getPopularEquipment();
        return Result.success(data);
    }
    
    @ApiOperation("获取热门教练排行")
    @GetMapping("/popular-coaches")
    public Result<Map<String, Object>> getPopularCoaches() {
        Map<String, Object> data = dashboardService.getPopularCoaches();
        return Result.success(data);
    }
    
    @ApiOperation("获取热门课程排行")
    @GetMapping("/popular-courses")
    public Result<Map<String, Object>> getPopularCourses() {
        Map<String, Object> data = dashboardService.getPopularCourses();
        return Result.success(data);
    }
}







