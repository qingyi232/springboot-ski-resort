package com.ski.resort.controller;

import com.ski.resort.common.Result;
import com.ski.resort.entity.Coach;
import com.ski.resort.entity.CoachBooking;
import com.ski.resort.entity.Course;
import com.ski.resort.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 统计数据控制器
 */
@Api(tags = "统计数据管理")
@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final UserService userService;
    private final RentalOrderService rentalOrderService;
    private final CourseService courseService;
    private final CoachBookingService coachBookingService;
    private final SalesOrderService salesOrderService;
    private final EquipmentService equipmentService;
    private final CoachService coachService;

    @ApiOperation("获取管理员统计数据")
    @GetMapping("/admin")
    public Result<Map<String, Object>> getAdminStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 1. 总用户数（排除管理员和教练）
        long totalUsers = userService.getAllUsers().stream()
                .filter(user -> "user".equals(user.getRole()))
                .count();
        
        // 2. 在租雪具数量（状态为1=使用中的租赁订单）
        long activeRentals = rentalOrderService.listAll().stream()
                .filter(order -> order.getOrderStatus() != null && order.getOrderStatus() == 1)
                .count();
        
        // 3. 进行中课程数（状态为2=进行中）
        long ongoingCourses = courseService.listAll().stream()
                .filter(course -> course.getStatus() != null && course.getStatus() == 2)
                .count();
        
        // 4. 今日收入（今天的销售订单 + 租赁订单 + 课程预约）
        LocalDate today = LocalDate.now();
        double todayIncome = 0.0;
        
        // 销售订单收入
        todayIncome += salesOrderService.getAllOrders().stream()
                .filter(order -> order.getCreateTime() != null 
                        && order.getCreateTime().toLocalDate().equals(today)
                        && order.getPaymentStatus() != null && order.getPaymentStatus() == 1)
                .mapToDouble(order -> order.getTotalAmount() != null ? order.getTotalAmount().doubleValue() : 0.0)
                .sum();
        
        // 租赁订单收入
        todayIncome += rentalOrderService.listAll().stream()
                .filter(order -> order.getCreateTime() != null 
                        && order.getCreateTime().toLocalDate().equals(today)
                        && order.getPaymentStatus() != null && order.getPaymentStatus() == 1)
                .mapToDouble(order -> order.getTotalAmount() != null ? order.getTotalAmount().doubleValue() : 0.0)
                .sum();
        
        // 课程预约收入
        todayIncome += coachBookingService.getAllBookings().stream()
                .filter(booking -> booking.getCreateTime() != null 
                        && booking.getCreateTime().toLocalDate().equals(today)
                        && booking.getPaymentStatus() != null && booking.getPaymentStatus() == 1)
                .mapToDouble(booking -> booking.getTotalAmount() != null ? booking.getTotalAmount().doubleValue() : 0.0)
                .sum();
        
        statistics.put("totalUsers", totalUsers);
        statistics.put("activeRentals", activeRentals);
        statistics.put("ongoingCourses", ongoingCourses);
        statistics.put("todayIncome", String.format("¥%.2f", todayIncome));
        
        // 5. 租赁趋势（最近7天）
        List<Map<String, Object>> rentalTrend = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            long count = rentalOrderService.listAll().stream()
                    .filter(order -> order.getCreateTime() != null 
                            && order.getCreateTime().toLocalDate().equals(date))
                    .count();
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", getWeekDay(date.getDayOfWeek().getValue()));
            dayData.put("count", count);
            rentalTrend.add(dayData);
        }
        statistics.put("rentalTrend", rentalTrend);
        
        // 6. 课程报名情况（按课程类型统计课程数量）
        Map<String, Long> courseTypeCount = new HashMap<>();
        // 直接从课程表统计各类型课程数量
        courseService.listAll().forEach(course -> {
            if (course.getCourseType() != null && !course.getCourseType().isEmpty()) {
                String courseType = course.getCourseType();
                courseTypeCount.put(courseType, courseTypeCount.getOrDefault(courseType, 0L) + 1);
            }
        });
        
        List<Map<String, Object>> courseDistribution = new ArrayList<>();
        courseTypeCount.forEach((type, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", type);
            item.put("value", count);
            courseDistribution.add(item);
        });
        statistics.put("courseDistribution", courseDistribution);
        
        // 7. 热门雪具（按租赁次数排序，取前5）
        Map<String, Long> equipmentRentalCount = new HashMap<>();
        rentalOrderService.listAll().forEach(order -> {
            String equipmentName = order.getEquipmentName();
            if (equipmentName != null && !equipmentName.isEmpty()) {
                equipmentRentalCount.put(equipmentName, equipmentRentalCount.getOrDefault(equipmentName, 0L) + 1);
            }
        });
        
        List<Map<String, Object>> hotEquipment = new ArrayList<>();
        equipmentRentalCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("equipmentName", entry.getKey());
                    item.put("rentalCount", entry.getValue());
                    hotEquipment.add(item);
                });
        statistics.put("hotEquipment", hotEquipment);
        
        // 8. 热门课程（按学员数排序，取前5）
        List<Map<String, Object>> hotCourses = new ArrayList<>();
        courseService.listAll().stream()
                .sorted((c1, c2) -> {
                    int students1 = c1.getCurrentStudents() != null ? c1.getCurrentStudents() : 0;
                    int students2 = c2.getCurrentStudents() != null ? c2.getCurrentStudents() : 0;
                    return Integer.compare(students2, students1);
                })
                .limit(5)
                .forEach(course -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("courseName", course.getCourseName());
                    item.put("currentStudents", course.getCurrentStudents() != null ? course.getCurrentStudents() : 0);
                    hotCourses.add(item);
                });
        statistics.put("hotCourses", hotCourses);
        
        // 9. 教练排行（按评分排序，取前5）
        List<Map<String, Object>> topCoaches = new ArrayList<>();
        coachService.listAll().stream()
                .sorted((c1, c2) -> {
                    double rating1 = c1.getRating() != null ? c1.getRating().doubleValue() : 0.0;
                    double rating2 = c2.getRating() != null ? c2.getRating().doubleValue() : 0.0;
                    return Double.compare(rating2, rating1);
                })
                .limit(5)
                .forEach(coach -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("coachName", coach.getCoachName());
                    item.put("rating", coach.getRating() != null ? coach.getRating().doubleValue() : 0.0);
                    topCoaches.add(item);
                });
        statistics.put("topCoaches", topCoaches);
        
        return Result.success(statistics);
    }

    @ApiOperation("获取教练统计数据")
    @GetMapping("/coach/{userId}")
    public Result<Map<String, Object>> getCoachStatistics(@PathVariable Long userId) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 根据用户ID获取教练信息
        Coach coach = coachService.getByUserId(userId);
        if (coach == null) {
            return Result.error("教练信息不存在");
        }
        
        Long coachId = coach.getId();
        
        // 获取该教练的所有预约（一次查询，避免重复）
        List<CoachBooking> allBookings = coachBookingService.getBookingsByCoachId(coachId);
        
        // 1. 学员总数（预约过该教练的不同学员数）
        long totalStudents = allBookings.stream()
                .map(CoachBooking::getUserId)
                .distinct()
                .count();
        
        // 2. 待确认预约数（状态为0）
        long pendingBookings = allBookings.stream()
                .filter(booking -> booking.getStatus() != null && booking.getStatus() == 0)
                .count();
        
        // 3. 进行中预约数（状态为2）- 修正：应该统计预约表，不是课程表
        long ongoingCourses = allBookings.stream()
                .filter(booking -> booking.getStatus() != null && booking.getStatus() == 2)
                .count();
        
        // 4. 已完成预约数（状态为3）
        long completedBookings = allBookings.stream()
                .filter(booking -> booking.getStatus() != null && booking.getStatus() == 3)
                .count();
        
        statistics.put("totalStudents", totalStudents);
        statistics.put("pendingBookings", pendingBookings);
        statistics.put("ongoingCourses", ongoingCourses);
        statistics.put("completedBookings", completedBookings);
        
        // 5. 预约趋势（最近7天）
        List<Map<String, Object>> bookingTrend = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            long count = coachBookingService.getBookingsByCoachId(coachId).stream()
                    .filter(booking -> booking.getCreateTime() != null 
                            && booking.getCreateTime().toLocalDate().equals(date))
                    .count();
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", getWeekDay(date.getDayOfWeek().getValue()));
            dayData.put("count", count);
            bookingTrend.add(dayData);
        }
        statistics.put("bookingTrend", bookingTrend);
        
        // 6. 课程类型分布（按该教练的课程类型统计）
        Map<String, Long> courseTypeCount = new HashMap<>();
        // 统计该教练的课程类型分布
        courseService.listByCoachId(coachId).forEach(course -> {
            if (course.getCourseType() != null && !course.getCourseType().isEmpty()) {
                String courseType = course.getCourseType();
                courseTypeCount.put(courseType, courseTypeCount.getOrDefault(courseType, 0L) + 1);
            }
        });
        
        List<Map<String, Object>> courseDistribution = new ArrayList<>();
        courseTypeCount.forEach((type, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", type);
            item.put("value", count);
            courseDistribution.add(item);
        });
        statistics.put("courseDistribution", courseDistribution);
        
        return Result.success(statistics);
    }
    
    /**
     * 获取星期几的中文名称
     */
    private String getWeekDay(int dayOfWeek) {
        String[] weekDays = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        return weekDays[dayOfWeek];
    }
}

