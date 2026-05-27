package com.ski.resort.service;

import java.util.Map;

/**
 * Dashboard统计Service接口
 */
public interface DashboardService {
    
    /**
     * 获取统计概览数据
     */
    Map<String, Object> getOverview();
    
    /**
     * 获取收入统计数据
     */
    Map<String, Object> getRevenueStats(String startDate, String endDate);
    
    /**
     * 获取订单统计数据
     */
    Map<String, Object> getOrderStats(String startDate, String endDate);
    
    /**
     * 获取热门雪具排行
     */
    Map<String, Object> getPopularEquipment();
    
    /**
     * 获取热门教练排行
     */
    Map<String, Object> getPopularCoaches();
    
    /**
     * 获取热门课程排行
     */
    Map<String, Object> getPopularCourses();
}







