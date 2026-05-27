package com.ski.resort.service;

import com.ski.resort.dto.AIRecommendationRequest;
import com.ski.resort.entity.Course;
import com.ski.resort.entity.Product;

import java.util.List;

/**
 * AI推荐服务接口
 */
public interface AIRecommendationService {
    
    /**
     * 推荐课程
     * 
     * @param request 推荐请求
     * @return 推荐的课程列表
     */
    List<Course> recommendCourses(AIRecommendationRequest request);
    
    /**
     * 推荐装备
     * 
     * @param request 推荐请求
     * @return 推荐的装备列表
     */
    List<Product> recommendEquipment(AIRecommendationRequest request);
    
    /**
     * 获取AI推荐理由
     * 
     * @param request 推荐请求
     * @param items 推荐项
     * @return 推荐理由
     */
    String getRecommendationReason(AIRecommendationRequest request, List<?> items);
}





