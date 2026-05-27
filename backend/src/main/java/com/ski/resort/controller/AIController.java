package com.ski.resort.controller;

import com.ski.resort.common.Result;
import com.ski.resort.dto.AIChatRequest;
import com.ski.resort.dto.AIRecommendationRequest;
import com.ski.resort.entity.Course;
import com.ski.resort.entity.Product;
import com.ski.resort.service.AIChatbotService;
import com.ski.resort.service.AIRecommendationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI服务Controller
 */
@Slf4j
@Api(tags = "AI智能服务")
@RestController
@RequestMapping("/api/v1/ai")
public class AIController {
    
    @Autowired
    private AIRecommendationService recommendationService;
    
    @Autowired
    private AIChatbotService chatbotService;
    
    @ApiOperation("AI课程推荐")
    @PostMapping("/recommend/courses")
    public Result<Map<String, Object>> recommendCourses(@RequestBody AIRecommendationRequest request) {
        log.info("收到AI课程推荐请求: {}", request);
        
        try {
            // 获取推荐课程
            List<Course> courses = recommendationService.recommendCourses(request);
            
            // 获取推荐理由
            String reason = recommendationService.getRecommendationReason(request, courses);
            
            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("courses", courses);
            result.put("reason", reason);
            result.put("count", courses.size());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("AI课程推荐失败: ", e);
            return Result.error("推荐失败: " + e.getMessage());
        }
    }
    
    @ApiOperation("AI装备推荐")
    @PostMapping("/recommend/equipment")
    public Result<Map<String, Object>> recommendEquipment(@RequestBody AIRecommendationRequest request) {
        log.info("收到AI装备推荐请求: {}", request);
        
        try {
            // 获取推荐装备
            List<Product> products = recommendationService.recommendEquipment(request);
            
            // 获取推荐理由
            String reason = recommendationService.getRecommendationReason(request, products);
            
            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("products", products);
            result.put("reason", reason);
            result.put("count", products.size());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("AI装备推荐失败: ", e);
            return Result.error("推荐失败: " + e.getMessage());
        }
    }
    
    @ApiOperation("AI智能客服")
    @PostMapping("/chatbot")
    public Result<Map<String, Object>> chatbot(@RequestBody AIChatRequest request) {
        log.info("收到AI聊天请求: {}", request.getMessage());
        
        try {
            // 生成会话ID（如果没有）
            if (request.getSessionId() == null || request.getSessionId().isEmpty()) {
                request.setSessionId("session_" + request.getUserId() + "_" + System.currentTimeMillis());
            }
            
            // 调用AI聊天
            String response = chatbotService.chat(request);
            
            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("message", response);
            result.put("sessionId", request.getSessionId());
            result.put("timestamp", System.currentTimeMillis());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("AI聊天失败: ", e);
            return Result.error("聊天失败: " + e.getMessage());
        }
    }
}





