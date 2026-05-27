package com.ski.resort.dto;

import lombok.Data;

/**
 * AI推荐请求DTO
 */
@Data
public class AIRecommendationRequest {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 推荐类型：course-课程推荐, equipment-装备推荐
     */
    private String type;
    
    /**
     * 用户滑雪水平：beginner-初级, intermediate-中级, advanced-高级
     */
    private String skillLevel;
    
    /**
     * 用户偏好（可选）
     */
    private String preference;
    
    /**
     * 预算范围（可选）
     */
    private Double budget;
}





