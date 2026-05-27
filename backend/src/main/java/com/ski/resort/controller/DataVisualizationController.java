package com.ski.resort.controller;

import com.ski.resort.common.Result;
import com.ski.resort.mapper.*;
import com.ski.resort.entity.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据可视化Controller
 */
@Slf4j
@Api(tags = "数据可视化")
@RestController
@RequestMapping("/api/v1/visualization")
public class DataVisualizationController {
    
    @Autowired
    private SalesOrderMapper salesOrderMapper;
    
    @Autowired
    private RentalOrderMapper rentalOrderMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private CoachBookingMapper coachBookingMapper;
    
    @Autowired
    private VenueBookingMapper venueBookingMapper;
    
    @ApiOperation("获取大屏统计数据")
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardData() {
        log.info("=== 获取数据可视化大屏数据 ===");
        
        try {
            Map<String, Object> data = new HashMap<>();
            
            // 1. 总体概览
            data.put("overview", getOverviewData());
            
            // 2. 收入趋势（最近7天）
            data.put("revenueTrend", getRevenueTrend());
            
            // 3. 课程统计
            data.put("courseStats", getCourseStats());
            
            // 4. 装备销售排行
            data.put("productRanking", getProductRanking());
            
            // 5. 用户增长趋势
            data.put("userGrowth", getUserGrowth());
            
            // 6. 场地预订情况
            data.put("venueBooking", getVenueBookingStats());
            
            // 7. 教练预约统计
            data.put("coachBooking", getCoachBookingStats());
            
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取可视化数据失败: ", e);
            return Result.error("获取数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 总体概览
     */
    private Map<String, Object> getOverviewData() {
        Map<String, Object> overview = new HashMap<>();
        
        // 总用户数
        List<User> users = userMapper.selectAll();
        overview.put("totalUsers", users.size());
        
        // 总收入（销售订单 + 租赁订单 + 场地预订 + 教练预约）
        List<SalesOrder> salesOrders = salesOrderMapper.selectAll();
        List<RentalOrder> rentalOrders = rentalOrderMapper.selectAll();
        List<VenueBooking> venueBookings = venueBookingMapper.selectAll();
        List<CoachBooking> coachBookings = coachBookingMapper.selectAll();
        
        double totalRevenue = salesOrders.stream()
                .map(o -> o.getActualAmount() != null ? o.getActualAmount().doubleValue() : 0.0)
                .reduce(0.0, Double::sum)
            + rentalOrders.stream()
                .map(o -> o.getTotalAmount() != null ? o.getTotalAmount().doubleValue() : 0.0)
                .reduce(0.0, Double::sum)
            + venueBookings.stream()
                .filter(v -> v.getPaymentStatus() != null && v.getPaymentStatus() == 1)
                .map(v -> v.getTotalAmount() != null ? v.getTotalAmount().doubleValue() : 0.0)
                .reduce(0.0, Double::sum)
            + coachBookings.stream()
                .filter(c -> c.getPaymentStatus() != null && c.getPaymentStatus() == 1)
                .map(c -> c.getTotalAmount() != null ? c.getTotalAmount().doubleValue() : 0.0)
                .reduce(0.0, Double::sum);
        
        overview.put("totalRevenue", Math.round(totalRevenue * 100.0) / 100.0);
        
        // 课程数量
        List<Course> courses = courseMapper.selectAll();
        overview.put("totalCourses", courses.size());
        
        // 商品数量
        List<Product> products = productMapper.selectAll();
        overview.put("totalProducts", products.size());
        
        // 今日订单数
        long todayOrders = salesOrders.stream()
                .filter(o -> isToday(o.getCreateTime()))
                .count();
        overview.put("todayOrders", todayOrders);
        
        // 活跃用户数（最近7天有订单的用户）
        Set<Long> activeUserIds = new HashSet<>();
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        
        salesOrders.stream()
                .filter(o -> o.getCreateTime() != null && 
                        o.getCreateTime().toLocalDate().isAfter(sevenDaysAgo))
                .forEach(o -> activeUserIds.add(o.getUserId()));
        
        overview.put("activeUsers", activeUserIds.size());
        
        return overview;
    }
    
    /**
     * 收入趋势（最近7天）
     */
    private Map<String, Object> getRevenueTrend() {
        Map<String, Object> trend = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Double> revenues = new ArrayList<>();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate today = LocalDate.now();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            dates.add(date.format(formatter));
            
            // 计算当天收入
            List<SalesOrder> salesOrders = salesOrderMapper.selectAll();
            List<RentalOrder> rentalOrders = rentalOrderMapper.selectAll();
            List<CoachBooking> coachBookings = coachBookingMapper.selectAll();
            List<VenueBooking> venueBookings = venueBookingMapper.selectAll();
            
            double dailyRevenue = salesOrders.stream()
                    .filter(o -> o.getCreateTime() != null && 
                            o.getCreateTime().toLocalDate().equals(date))
                    .map(o -> o.getActualAmount() != null ? o.getActualAmount().doubleValue() : 0.0)
                    .reduce(0.0, Double::sum)
                + rentalOrders.stream()
                    .filter(o -> o.getCreateTime() != null && 
                            o.getCreateTime().toLocalDate().equals(date))
                    .map(o -> o.getTotalAmount() != null ? o.getTotalAmount().doubleValue() : 0.0)
                    .reduce(0.0, Double::sum)
                + coachBookings.stream()
                    .filter(c -> c.getPaymentTime() != null && 
                            c.getPaymentTime().toLocalDate().equals(date) &&
                            c.getPaymentStatus() != null && c.getPaymentStatus() == 1)
                    .map(c -> c.getTotalAmount() != null ? c.getTotalAmount().doubleValue() : 0.0)
                    .reduce(0.0, Double::sum)
                + venueBookings.stream()
                    .filter(v -> v.getPaymentTime() != null && 
                            v.getPaymentTime().toLocalDate().equals(date) &&
                            v.getPaymentStatus() != null && v.getPaymentStatus() == 1)
                    .map(v -> v.getTotalAmount() != null ? v.getTotalAmount().doubleValue() : 0.0)
                    .reduce(0.0, Double::sum);
            
            revenues.add(Math.round(dailyRevenue * 100.0) / 100.0);
        }
        
        trend.put("dates", dates);
        trend.put("revenues", revenues);
        
        return trend;
    }
    
    /**
     * 课程统计
     */
    private Map<String, Object> getCourseStats() {
        Map<String, Object> stats = new HashMap<>();
        
        List<Course> courses = courseMapper.selectAll();
        
        // 按难度统计
        Map<String, Long> difficultyCount = courses.stream()
                .collect(Collectors.groupingBy(
                    c -> c.getDifficulty() != null ? c.getDifficulty() : "未知",
                    Collectors.counting()
                ));
        
        stats.put("difficultyCount", difficultyCount);
        
        // 热门课程Top5
        List<Map<String, Object>> hotCourses = courses.stream()
                .filter(c -> c.getIsHot() != null && c.getIsHot() == 1)
                .limit(5)
                .map(c -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", c.getCourseName());
                    map.put("price", c.getCoursePrice());
                    map.put("currentStudents", c.getCurrentStudents());
                    return map;
                })
                .collect(Collectors.toList());
        
        stats.put("hotCourses", hotCourses);
        
        return stats;
    }
    
    /**
     * 装备销售排行Top10
     */
    private List<Map<String, Object>> getProductRanking() {
        List<Product> products = productMapper.selectAll();
        
        return products.stream()
                .sorted(Comparator.comparing(Product::getSoldQuantity, Comparator.reverseOrder()))
                .limit(10)
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", p.getProductName());
                    map.put("sold", p.getSoldQuantity());
                    map.put("revenue", p.getPrice().multiply(new BigDecimal(p.getSoldQuantity())));
                    return map;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 用户增长趋势（最近30天）
     */
    private Map<String, Object> getUserGrowth() {
        Map<String, Object> growth = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        
        List<User> users = userMapper.selectAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate today = LocalDate.now();
        
        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            dates.add(date.format(formatter));
            
            // 计算截止当天的累计用户数
            long count = users.stream()
                    .filter(u -> u.getCreateTime() != null && 
                            !u.getCreateTime().toLocalDate().isAfter(date))
                    .count();
            
            counts.add((int) count);
        }
        
        growth.put("dates", dates);
        growth.put("counts", counts);
        
        return growth;
    }
    
    /**
     * 场地预订统计
     */
    private Map<String, Object> getVenueBookingStats() {
        Map<String, Object> stats = new HashMap<>();
        
        List<VenueBooking> bookings = venueBookingMapper.selectAll();
        
        // 按状态统计
        Map<String, Long> statusCount = new HashMap<>();
        statusCount.put("待确认", bookings.stream().filter(b -> b.getStatus() == 0).count());
        statusCount.put("已确认", bookings.stream().filter(b -> b.getStatus() == 1).count());
        statusCount.put("使用中", bookings.stream().filter(b -> b.getStatus() == 2).count());
        statusCount.put("已完成", bookings.stream().filter(b -> b.getStatus() == 3).count());
        statusCount.put("已取消", bookings.stream().filter(b -> b.getStatus() == 4).count());
        
        stats.put("statusCount", statusCount);
        
        return stats;
    }
    
    /**
     * 教练预约统计
     */
    private Map<String, Object> getCoachBookingStats() {
        Map<String, Object> stats = new HashMap<>();
        
        List<CoachBooking> bookings = coachBookingMapper.selectAll();
        
        // 按教练统计
        Map<String, Long> coachCount = bookings.stream()
                .collect(Collectors.groupingBy(
                    b -> b.getCoachName() != null ? b.getCoachName() : "未知",
                    Collectors.counting()
                ));
        
        stats.put("coachCount", coachCount);
        
        // 按状态统计
        Map<String, Long> statusCount = new HashMap<>();
        statusCount.put("待确认", bookings.stream().filter(b -> b.getStatus() == 0).count());
        statusCount.put("已确认", bookings.stream().filter(b -> b.getStatus() == 1).count());
        statusCount.put("进行中", bookings.stream().filter(b -> b.getStatus() == 2).count());
        statusCount.put("已完成", bookings.stream().filter(b -> b.getStatus() == 3).count());
        statusCount.put("已取消", bookings.stream().filter(b -> b.getStatus() == 4).count());
        
        stats.put("statusCount", statusCount);
        
        return stats;
    }
    
    /**
     * 判断是否是今天
     */
    private boolean isToday(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return false;
        return dateTime.toLocalDate().equals(LocalDate.now());
    }
}





