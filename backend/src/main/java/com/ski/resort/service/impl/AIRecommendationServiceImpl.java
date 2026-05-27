package com.ski.resort.service.impl;

import com.ski.resort.dto.AIRecommendationRequest;
import com.ski.resort.entity.Course;
import com.ski.resort.entity.Product;
import com.ski.resort.mapper.CourseMapper;
import com.ski.resort.mapper.ProductMapper;
import com.ski.resort.service.AIRecommendationService;
import com.ski.resort.service.DeepSeekService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI推荐服务实现
 */
@Slf4j
@Service
public class AIRecommendationServiceImpl implements AIRecommendationService {
    
    @Autowired
    private DeepSeekService deepSeekService;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    public List<Course> recommendCourses(AIRecommendationRequest request) {
        log.info("=== AI课程推荐 ===");
        log.info("用户水平: {}, 偏好: {}, 预算: {}", 
                request.getSkillLevel(), request.getPreference(), request.getBudget());
        
        // 1. 获取所有可用课程
        List<Course> allCourses = courseMapper.selectAll();
        
        // 2. 根据用户水平筛选
        List<Course> filteredCourses = filterCoursesBySkillLevel(allCourses, request.getSkillLevel());
        
        // 3. 根据预算筛选
        if (request.getBudget() != null) {
            filteredCourses = filteredCourses.stream()
                    .filter(c -> c.getCoursePrice().doubleValue() <= request.getBudget())
                    .collect(Collectors.toList());
        }
        
        // 4. 按评分和价格排序，取前5个
        filteredCourses = filteredCourses.stream()
                .sorted(Comparator.comparing(Course::getIsHot, Comparator.reverseOrder())
                        .thenComparing(Course::getCoursePrice))
                .limit(5)
                .collect(Collectors.toList());
        
        log.info("推荐课程数量: {}", filteredCourses.size());
        return filteredCourses;
    }
    
    @Override
    public List<Product> recommendEquipment(AIRecommendationRequest request) {
        log.info("=== AI装备推荐 ===");
        log.info("用户水平: {}, 偏好: {}, 预算: {}", 
                request.getSkillLevel(), request.getPreference(), request.getBudget());
        
        // 1. 获取所有可用装备
        List<Product> allProducts = productMapper.selectAll();
        
        // 2. 根据用户水平筛选
        List<Product> filteredProducts = filterProductsBySkillLevel(allProducts, request.getSkillLevel());
        
        // 3. 根据预算筛选
        if (request.getBudget() != null) {
            filteredProducts = filteredProducts.stream()
                    .filter(p -> p.getPrice().doubleValue() <= request.getBudget())
                    .collect(Collectors.toList());
        }
        
        // 4. 按热门和价格排序，取前5个
        filteredProducts = filteredProducts.stream()
                .sorted(Comparator.comparing(Product::getIsHot, Comparator.reverseOrder())
                        .thenComparing(Product::getIsNew, Comparator.reverseOrder())
                        .thenComparing(Product::getPrice))
                .limit(5)
                .collect(Collectors.toList());
        
        log.info("推荐装备数量: {}", filteredProducts.size());
        return filteredProducts;
    }
    
    @Override
    public String getRecommendationReason(AIRecommendationRequest request, List<?> items) {
        if (items == null || items.isEmpty()) {
            return "暂无推荐";
        }
        
        // 构建推荐项信息
        StringBuilder itemsInfo = new StringBuilder();
        for (Object item : items) {
            if (item instanceof Course) {
                Course course = (Course) item;
                itemsInfo.append(String.format("- %s (难度:%s, 价格:¥%.0f)\n", 
                        course.getCourseName(), course.getDifficulty(), course.getCoursePrice()));
            } else if (item instanceof Product) {
                Product product = (Product) item;
                itemsInfo.append(String.format("- %s (品牌:%s, 价格:¥%.0f)\n", 
                        product.getProductName(), product.getBrand(), product.getPrice()));
            }
        }
        
        // 构建AI提示词
        String prompt = String.format(
                "作为滑雪场的专业顾问，根据以下用户信息和推荐列表，用简洁专业的语言（100字以内）说明推荐理由：\n" +
                "用户水平: %s\n" +
                "偏好: %s\n" +
                "预算: %s元\n\n" +
                "推荐列表：\n%s\n" +
                "请用友好、鼓励的语气解释为什么这些选项适合该用户。",
                request.getSkillLevel() != null ? request.getSkillLevel() : "未知",
                request.getPreference() != null ? request.getPreference() : "无特殊偏好",
                request.getBudget() != null ? request.getBudget() : "不限",
                itemsInfo.toString()
        );
        
        // 调用AI获取推荐理由
        try {
            String reason = deepSeekService.chatSimple(
                    "你是飞跃滑雪场的专业课程顾问，擅长根据用户情况提供个性化推荐。",
                    prompt
            );
            return reason != null ? reason : "根据您的水平和需求，我们精选了以上选项供您参考。";
        } catch (Exception e) {
            log.error("获取AI推荐理由失败: ", e);
            return "根据您的水平和需求，我们精选了以上选项供您参考。";
        }
    }
    
    /**
     * 根据用户水平筛选课程
     */
    private List<Course> filterCoursesBySkillLevel(List<Course> courses, String skillLevel) {
        if (skillLevel == null) {
            return courses;
        }
        
        return courses.stream()
                .filter(c -> {
                    String difficulty = c.getDifficulty();
                    if (difficulty == null) return false;
                    
                    switch (skillLevel.toLowerCase()) {
                        case "beginner":
                        case "初级":
                            return difficulty.contains("初级") || difficulty.contains("入门");
                        case "intermediate":
                        case "中级":
                            return difficulty.contains("中级") || difficulty.contains("进阶");
                        case "advanced":
                        case "高级":
                            return difficulty.contains("高级") || difficulty.contains("提升");
                        default:
                            return true;
                    }
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 根据用户水平筛选装备
     */
    private List<Product> filterProductsBySkillLevel(List<Product> products, String skillLevel) {
        if (skillLevel == null) {
            return products;
        }
        
        return products.stream()
                .filter(p -> {
                    String category = p.getCategory();
                    String name = p.getProductName();
                    
                    if (category == null) return false;
                    
                    // 根据水平推荐不同类型的装备
                    switch (skillLevel.toLowerCase()) {
                        case "beginner":
                        case "初级":
                            // 初级用户推荐基础装备
                            return !name.contains("竞技") && !name.contains("高端");
                        case "intermediate":
                        case "中级":
                            // 中级用户可以使用各种装备
                            return true;
                        case "advanced":
                        case "高级":
                            // 高级用户推荐高端装备
                            return name.contains("X") || name.contains("Pro") || 
                                   name.contains("竞技") || p.getPrice().doubleValue() > 2000;
                        default:
                            return true;
                    }
                })
                .collect(Collectors.toList());
    }
}





