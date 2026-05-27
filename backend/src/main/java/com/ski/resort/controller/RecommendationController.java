package com.ski.resort.controller;

import com.ski.resort.common.Result;
import com.ski.resort.entity.Coach;
import com.ski.resort.entity.Course;
import com.ski.resort.entity.Equipment;
import com.ski.resort.entity.Product;
import com.ski.resort.service.RecommendationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI智能推荐Controller
 *
 * @author XXX
 * @date 2025-10-24
 */
@Slf4j
@Api(tags = "AI智能推荐")
@RestController
@RequestMapping("/api/v1/recommendations")
@CrossOrigin
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    /**
     * 为用户推荐雪具
     */
    @ApiOperation("个性化推荐雪具")
    @GetMapping("/equipment/{userId}")
    public Result<List<Equipment>> recommendEquipment(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("推荐数量") @RequestParam(defaultValue = "6") int limit) {
        
        log.info("为用户{}推荐雪具", userId);
        List<Equipment> recommendations = recommendationService.recommendEquipment(userId, limit);
        return Result.success("推荐成功", recommendations);
    }

    /**
     * 为用户推荐教练
     */
    @ApiOperation("个性化推荐教练")
    @GetMapping("/coach/{userId}")
    public Result<List<Coach>> recommendCoach(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("推荐数量") @RequestParam(defaultValue = "6") int limit) {
        
        log.info("为用户{}推荐教练", userId);
        List<Coach> recommendations = recommendationService.recommendCoach(userId, limit);
        return Result.success("推荐成功", recommendations);
    }

    /**
     * 为用户推荐课程
     */
    @ApiOperation("个性化推荐课程")
    @GetMapping("/course/{userId}")
    public Result<List<Course>> recommendCourse(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("推荐数量") @RequestParam(defaultValue = "6") int limit) {
        
        log.info("为用户{}推荐课程", userId);
        List<Course> recommendations = recommendationService.recommendCourse(userId, limit);
        return Result.success("推荐成功", recommendations);
    }

    /**
     * 为用户推荐商品
     */
    @ApiOperation("个性化推荐商品")
    @GetMapping("/product/{userId}")
    public Result<List<Product>> recommendProduct(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("推荐数量") @RequestParam(defaultValue = "6") int limit) {
        
        log.info("为用户{}推荐商品", userId);
        List<Product> recommendations = recommendationService.recommendProduct(userId, limit);
        return Result.success("推荐成功", recommendations);
    }

    /**
     * 获取热门雪具（无需登录）
     */
    @ApiOperation("获取热门雪具")
    @GetMapping("/hot/equipment")
    public Result<List<Equipment>> getHotEquipment(
            @ApiParam("数量") @RequestParam(defaultValue = "6") int limit) {
        
        List<Equipment> hotEquipment = recommendationService.getHotEquipment(limit);
        return Result.success("获取成功", hotEquipment);
    }

    /**
     * 获取热门教练（无需登录）
     */
    @ApiOperation("获取热门教练")
    @GetMapping("/hot/coach")
    public Result<List<Coach>> getHotCoach(
            @ApiParam("数量") @RequestParam(defaultValue = "6") int limit) {
        
        List<Coach> hotCoaches = recommendationService.getHotCoach(limit);
        return Result.success("获取成功", hotCoaches);
    }

    /**
     * 获取热门课程（无需登录）
     */
    @ApiOperation("获取热门课程")
    @GetMapping("/hot/course")
    public Result<List<Course>> getHotCourse(
            @ApiParam("数量") @RequestParam(defaultValue = "6") int limit) {
        
        List<Course> hotCourses = recommendationService.getHotCourse(limit);
        return Result.success("获取成功", hotCourses);
    }

    /**
     * 获取首页综合推荐（雪具+教练+课程）
     */
    @ApiOperation("获取首页综合推荐")
    @GetMapping("/home/{userId}")
    public Result<Map<String, Object>> getHomeRecommendations(
            @ApiParam("用户ID") @PathVariable Long userId) {
        
        log.info("获取用户{}的首页推荐", userId);
        
        Map<String, Object> recommendations = new HashMap<>();
        recommendations.put("equipment", recommendationService.recommendEquipment(userId, 4));
        recommendations.put("coaches", recommendationService.recommendCoach(userId, 4));
        recommendations.put("courses", recommendationService.recommendCourse(userId, 4));
        recommendations.put("products", recommendationService.recommendProduct(userId, 4));
        
        return Result.success("获取成功", recommendations);
    }

    /**
     * 获取热门推荐（无需登录，用于首页展示）
     */
    @ApiOperation("获取热门推荐")
    @GetMapping("/hot/all")
    public Result<Map<String, Object>> getAllHotRecommendations() {
        
        Map<String, Object> recommendations = new HashMap<>();
        recommendations.put("equipment", recommendationService.getHotEquipment(4));
        recommendations.put("coaches", recommendationService.getHotCoach(4));
        recommendations.put("courses", recommendationService.getHotCourse(4));
        
        return Result.success("获取成功", recommendations);
    }
}







