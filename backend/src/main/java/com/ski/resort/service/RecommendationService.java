package com.ski.resort.service;

import com.ski.resort.entity.Coach;
import com.ski.resort.entity.Course;
import com.ski.resort.entity.Equipment;
import com.ski.resort.entity.Product;

import java.util.List;

/**
 * AI智能推荐服务接口
 * 基于用户历史行为和偏好进行个性化推荐
 *
 * @author XXX
 * @date 2025-10-24
 */
public interface RecommendationService {

    /**
     * 推荐雪具（基于协同过滤算法）
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐的雪具列表
     */
    List<Equipment> recommendEquipment(Long userId, int limit);

    /**
     * 推荐教练（基于评分和专长匹配）
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐的教练列表
     */
    List<Coach> recommendCoach(Long userId, int limit);

    /**
     * 推荐课程（基于用户水平和学习历史）
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐的课程列表
     */
    List<Course> recommendCourse(Long userId, int limit);

    /**
     * 推荐商品（基于购买历史和浏览记录）
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐的商品列表
     */
    List<Product> recommendProduct(Long userId, int limit);

    /**
     * 获取热门雪具推荐（无需登录）
     * @param limit 推荐数量
     * @return 热门雪具列表
     */
    List<Equipment> getHotEquipment(int limit);

    /**
     * 获取热门教练推荐（无需登录）
     * @param limit 推荐数量
     * @return 热门教练列表
     */
    List<Coach> getHotCoach(int limit);

    /**
     * 获取热门课程推荐（无需登录）
     * @param limit 推荐数量
     * @return 热门课程列表
     */
    List<Course> getHotCourse(int limit);
}







