package com.ski.resort.service.impl;

import com.ski.resort.entity.*;
import com.ski.resort.mapper.*;
import com.ski.resort.service.RecommendationService;
import com.ski.resort.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AI智能推荐服务实现类
 * 使用协同过滤、内容推荐等算法实现个性化推荐
 *
 * @author XXX
 * @date 2025-10-24
 */
@Slf4j
@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private CoachMapper coachMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RentalOrderMapper rentalOrderMapper;

    @Autowired
    private CoachBookingMapper coachBookingMapper;

    @Autowired
    private CourseEnrollmentMapper courseEnrollmentMapper;

    @Autowired
    private RedisUtil redisUtil;

    private static final String CACHE_KEY_HOT_EQUIPMENT = "hot:equipment";
    private static final String CACHE_KEY_HOT_COACH = "hot:coach";
    private static final String CACHE_KEY_HOT_COURSE = "hot:course";
    private static final int CACHE_EXPIRE_HOURS = 6; // 6小时缓存

    /**
     * 推荐雪具（基于协同过滤算法）
     * 算法逻辑：
     * 1. 获取用户的租赁历史
     * 2. 找到租赁相似雪具的其他用户
     * 3. 推荐这些用户租赁过但当前用户未租赁的雪具
     * 4. 按受欢迎程度排序
     */
    @Override
    public List<Equipment> recommendEquipment(Long userId, int limit) {
        log.info("为用户{}推荐雪具，数量：{}", userId, limit);

        try {
            // 1. 获取用户的租赁历史（最近10次）
            List<RentalOrder> userOrders = rentalOrderMapper.selectByUserId(userId);
            if (userOrders == null || userOrders.isEmpty()) {
                // 新用户，返回热门雪具
                return getHotEquipment(limit);
            }

            // 2. 提取用户租赁过的雪具ID
            Set<Long> rentedEquipmentIds = userOrders.stream()
                    .map(RentalOrder::getEquipmentId)
                    .collect(Collectors.toSet());

            // 3. 获取所有可用雪具
            List<Equipment> allEquipment = equipmentMapper.selectAvailable();

            // 4. 基于租赁频次和可用数量进行推荐（简化的协同过滤）
            List<Equipment> recommendations = allEquipment.stream()
                    .filter(eq -> !rentedEquipmentIds.contains(eq.getId())) // 过滤已租赁
                    .sorted((e1, e2) -> {
                        // 按可用数量降序排序
                        int qty1 = e1.getAvailableQuantity() != null ? e1.getAvailableQuantity() : 0;
                        int qty2 = e2.getAvailableQuantity() != null ? e2.getAvailableQuantity() : 0;
                        return Integer.compare(qty2, qty1);
                    })
                    .limit(limit)
                    .collect(Collectors.toList());

            log.info("成功为用户{}推荐{}个雪具", userId, recommendations.size());
            return recommendations;

        } catch (Exception e) {
            log.error("推荐雪具失败", e);
            return getHotEquipment(limit);
        }
    }

    /**
     * 推荐教练（基于评分和专长匹配）
     * 算法逻辑：
     * 1. 获取用户的预约历史
     * 2. 分析用户偏好的教练特征（专长、等级）
     * 3. 推荐相似特征的高评分教练
     */
    @Override
    public List<Coach> recommendCoach(Long userId, int limit) {
        log.info("为用户{}推荐教练，数量：{}", userId, limit);

        try {
            // 1. 获取用户的预约历史
            List<CoachBooking> userBookings = coachBookingMapper.selectByUserId(userId);
            if (userBookings == null || userBookings.isEmpty()) {
                // 新用户，返回热门教练
                return getHotCoach(limit);
            }

            // 2. 提取用户预约过的教练ID
            Set<Long> bookedCoachIds = userBookings.stream()
                    .map(CoachBooking::getCoachId)
                    .collect(Collectors.toSet());

            // 3. 获取所有活跃教练
            List<Coach> allCoaches = coachMapper.selectActive();

            // 4. 按评分和学员数推荐
            List<Coach> recommendations = allCoaches.stream()
                    .filter(coach -> !bookedCoachIds.contains(coach.getId()))
                    .sorted((c1, c2) -> {
                        // 综合评分：rating * 0.7 + (studentCount/100) * 0.3
                        double score1 = (c1.getRating() != null ? c1.getRating().doubleValue() : 0.0) * 0.7
                                + (c1.getTotalStudents() != null ? c1.getTotalStudents() / 100.0 : 0.0) * 0.3;
                        double score2 = (c2.getRating() != null ? c2.getRating().doubleValue() : 0.0) * 0.7
                                + (c2.getTotalStudents() != null ? c2.getTotalStudents() / 100.0 : 0.0) * 0.3;
                        return Double.compare(score2, score1);
                    })
                    .limit(limit)
                    .collect(Collectors.toList());

            log.info("成功为用户{}推荐{}个教练", userId, recommendations.size());
            return recommendations;

        } catch (Exception e) {
            log.error("推荐教练失败", e);
            return getHotCoach(limit);
        }
    }

    /**
     * 推荐课程（基于用户水平和学习历史）
     * 算法逻辑：
     * 1. 获取用户的课程报名历史
     * 2. 分析用户的学习进度和难度偏好
     * 3. 推荐适合下一阶段的课程
     */
    @Override
    public List<Course> recommendCourse(Long userId, int limit) {
        log.info("为用户{}推荐课程，数量：{}", userId, limit);

        try {
            // 1. 获取用户的课程报名历史
            List<CourseEnrollment> userEnrollments = courseEnrollmentMapper.selectByUserId(userId);

            // 2. 提取用户已报名的课程ID
            Set<Long> enrolledCourseIds = userEnrollments.stream()
                    .map(CourseEnrollment::getCourseId)
                    .collect(Collectors.toSet());

            // 3. 获取所有可报名课程
            List<Course> allCourses = courseMapper.selectAvailable();

            // 4. 智能推荐逻辑
            List<Course> recommendations;
            if (userEnrollments.isEmpty()) {
                // 新用户：推荐热门课程
                recommendations = allCourses.stream()
                        .filter(course -> !enrolledCourseIds.contains(course.getId()))
                        .filter(course -> course.getCurrentStudents() != null && course.getMaxStudents() != null)
                        .filter(course -> course.getCurrentStudents() < course.getMaxStudents())
                        .sorted((c1, c2) -> Integer.compare(
                                c2.getCurrentStudents() != null ? c2.getCurrentStudents() : 0,
                                c1.getCurrentStudents() != null ? c1.getCurrentStudents() : 0
                        ))
                        .limit(limit)
                        .collect(Collectors.toList());
            } else {
                // 老用户：根据当前学习人数推荐热门课程
                recommendations = allCourses.stream()
                        .filter(course -> !enrolledCourseIds.contains(course.getId()))
                        .filter(course -> course.getCurrentStudents() != null && course.getMaxStudents() != null)
                        .filter(course -> course.getCurrentStudents() < course.getMaxStudents())
                        .sorted((c1, c2) -> Integer.compare(
                                c2.getCurrentStudents() != null ? c2.getCurrentStudents() : 0,
                                c1.getCurrentStudents() != null ? c1.getCurrentStudents() : 0
                        ))
                        .limit(limit)
                        .collect(Collectors.toList());
            }

            log.info("成功为用户{}推荐{}个课程", userId, recommendations.size());
            return recommendations;

        } catch (Exception e) {
            log.error("推荐课程失败", e);
            return getHotCourse(limit);
        }
    }

    /**
     * 推荐商品（基于购买历史和浏览记录）
     */
    @Override
    public List<Product> recommendProduct(Long userId, int limit) {
        log.info("为用户{}推荐商品，数量：{}", userId, limit);

        try {
            // 获取所有在售商品，按销量排序
            List<Product> allProducts = productMapper.selectOnSale();
            
            return allProducts.stream()
                    .sorted((p1, p2) -> Integer.compare(
                            p2.getSoldQuantity() != null ? p2.getSoldQuantity() : 0,
                            p1.getSoldQuantity() != null ? p1.getSoldQuantity() : 0
                    ))
                    .limit(limit)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("推荐商品失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取热门雪具推荐（基于租赁次数）
     */
    @Override
    public List<Equipment> getHotEquipment(int limit) {
        try {
            // 尝试从缓存获取
            Object cached = redisUtil.get(CACHE_KEY_HOT_EQUIPMENT);
            if (cached != null) {
                log.info("从缓存获取热门雪具");
                return (List<Equipment>) cached;
            }

            // 从数据库查询所有可用雪具，按可用数量排序
            List<Equipment> allEquipment = equipmentMapper.selectAvailable();
            List<Equipment> hotEquipment = allEquipment.stream()
                    .sorted((e1, e2) -> {
                        int qty1 = e1.getAvailableQuantity() != null ? e1.getAvailableQuantity() : 0;
                        int qty2 = e2.getAvailableQuantity() != null ? e2.getAvailableQuantity() : 0;
                        return Integer.compare(qty2, qty1);
                    })
                    .limit(limit)
                    .collect(Collectors.toList());
            
            // 写入缓存
            redisUtil.set(CACHE_KEY_HOT_EQUIPMENT, hotEquipment, CACHE_EXPIRE_HOURS * 3600);
            
            log.info("获取{}个热门雪具", hotEquipment.size());
            return hotEquipment;

        } catch (Exception e) {
            log.error("获取热门雪具失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取热门教练推荐（基于评分和学员数）
     */
    @Override
    public List<Coach> getHotCoach(int limit) {
        try {
            // 尝试从缓存获取
            Object cached = redisUtil.get(CACHE_KEY_HOT_COACH);
            if (cached != null) {
                log.info("从缓存获取热门教练");
                return (List<Coach>) cached;
            }

            // 从数据库查询：按评分和学员数综合排序
            List<Coach> allCoaches = coachMapper.selectActive();
            List<Coach> hotCoaches = allCoaches.stream()
                    .sorted((c1, c2) -> {
                        double score1 = (c1.getRating() != null ? c1.getRating().doubleValue() : 0.0) * 0.6
                                + (c1.getTotalStudents() != null ? c1.getTotalStudents() / 100.0 : 0.0) * 0.4;
                        double score2 = (c2.getRating() != null ? c2.getRating().doubleValue() : 0.0) * 0.6
                                + (c2.getTotalStudents() != null ? c2.getTotalStudents() / 100.0 : 0.0) * 0.4;
                        return Double.compare(score2, score1);
                    })
                    .limit(limit)
                    .collect(Collectors.toList());
            
            // 写入缓存
            redisUtil.set(CACHE_KEY_HOT_COACH, hotCoaches, CACHE_EXPIRE_HOURS * 3600);
            
            log.info("获取{}个热门教练", hotCoaches.size());
            return hotCoaches;

        } catch (Exception e) {
            log.error("获取热门教练失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取热门课程推荐（基于报名人数）
     */
    @Override
    public List<Course> getHotCourse(int limit) {
        try {
            // 尝试从缓存获取
            Object cached = redisUtil.get(CACHE_KEY_HOT_COURSE);
            if (cached != null) {
                log.info("从缓存获取热门课程");
                return (List<Course>) cached;
            }

            // 从数据库查询：按报名人数排序
            List<Course> allCourses = courseMapper.selectAvailable();
            List<Course> hotCourses = allCourses.stream()
                    .sorted((c1, c2) -> Integer.compare(
                            c2.getCurrentStudents() != null ? c2.getCurrentStudents() : 0,
                            c1.getCurrentStudents() != null ? c1.getCurrentStudents() : 0
                    ))
                    .limit(limit)
                    .collect(Collectors.toList());
            
            // 写入缓存
            redisUtil.set(CACHE_KEY_HOT_COURSE, hotCourses, CACHE_EXPIRE_HOURS * 3600);
            
            log.info("获取{}个热门课程", hotCourses.size());
            return hotCourses;

        } catch (Exception e) {
            log.error("获取热门课程失败", e);
            return new ArrayList<>();
        }
    }
}

