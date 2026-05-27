package com.ski.resort.controller;

import com.ski.resort.common.Result;
import com.ski.resort.entity.CoachBooking;
import com.ski.resort.service.CoachBookingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 教练预约Controller
 */
@Api(tags = "教练预约管理")
@RestController
@RequestMapping("/api/v1/bookings")
@CrossOrigin
public class CoachBookingController {
    
    @Autowired
    private CoachBookingService coachBookingService;
    
    @ApiOperation("查询所有预约")
    @GetMapping
    public Result<Map<String, Object>> getAllBookings(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long coachId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String date) {
        
        List<CoachBooking> allBookings = coachBookingService.getAllBookings();
        
        // 根据条件筛选
        List<CoachBooking> filteredBookings = allBookings.stream()
            .filter(booking -> {
                // 按教练筛选
                if (coachId != null && !coachId.equals(booking.getCoachId())) {
                    return false;
                }
                // 按状态筛选
                if (status != null && !status.isEmpty()) {
                    String bookingStatus = getStatusString(booking.getStatus());
                    if (!status.equals(bookingStatus)) {
                        return false;
                    }
                }
                // 按日期筛选
                if (date != null && !date.isEmpty()) {
                    String bookingDate = booking.getBookingDate() != null ? 
                        booking.getBookingDate().toString() : "";
                    if (!bookingDate.startsWith(date)) {
                        return false;
                    }
                }
                return true;
            })
            .collect(java.util.stream.Collectors.toList());
        
        // 计算分页
        int total = filteredBookings.size();
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);
        
        List<CoachBooking> pageBookings = startIndex < total ? 
            filteredBookings.subList(startIndex, endIndex) : 
            new java.util.ArrayList<>();
        
        // 返回分页结果
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("records", pageBookings);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        
        return Result.success(result);
    }
    
    // 辅助方法：将状态码转换为状态字符串
    private String getStatusString(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "pending";
            case 1: return "confirmed";
            case 2: return "ongoing";
            case 3: return "completed";
            case 4: return "cancelled";
            default: return "";
        }
    }
    
    @ApiOperation("根据ID查询预约")
    @GetMapping("/{id}")
    public Result<CoachBooking> getBookingById(@PathVariable Long id) {
        CoachBooking booking = coachBookingService.getBookingById(id);
        return Result.success(booking);
    }
    
    @ApiOperation("根据用户ID查询预约列表")
    @GetMapping("/user/{userId}")
    public Result<List<CoachBooking>> getBookingsByUserId(@PathVariable Long userId) {
        List<CoachBooking> bookings = coachBookingService.getBookingsByUserId(userId);
        return Result.success(bookings);
    }
    
    @ApiOperation("根据教练ID查询预约列表")
    @GetMapping("/coach/{coachId}")
    public Result<List<CoachBooking>> getBookingsByCoachId(@PathVariable Long coachId) {
        List<CoachBooking> bookings = coachBookingService.getBookingsByCoachId(coachId);
        return Result.success(bookings);
    }
    
    @ApiOperation("根据教练用户ID查询预约列表（自动转换为教练ID）")
    @GetMapping("/coach/user/{userId}")
    public Result<List<CoachBooking>> getBookingsByCoachUserId(@PathVariable Long userId) {
        List<CoachBooking> bookings = coachBookingService.getBookingsByCoachUserId(userId);
        return Result.success(bookings);
    }
    
    @ApiOperation("创建预约")
    @PostMapping
    public Result<Long> createBooking(@RequestBody CoachBooking coachBooking) {
        Long id = coachBookingService.createBooking(coachBooking);
        return Result.success(id);
    }
    
    @ApiOperation("更新预约")
    @PutMapping("/{id}")
    public Result<Void> updateBooking(@PathVariable Long id, @RequestBody CoachBooking coachBooking) {
        coachBooking.setId(id);
        coachBookingService.updateBooking(coachBooking);
        return Result.success();
    }
    
    @ApiOperation("取消预约")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancelBooking(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String cancelReason = params.get("cancelReason");
        coachBookingService.cancelBooking(id, cancelReason);
        return Result.success();
    }
    
    @ApiOperation("删除预约")
    @DeleteMapping("/{id}")
    public Result<Void> deleteBooking(@PathVariable Long id) {
        coachBookingService.deleteBooking(id);
        return Result.success();
    }
    
    @ApiOperation("确认预约")
    @PutMapping("/{id}/confirm")
    public Result<Void> confirmBooking(@PathVariable Long id) {
        coachBookingService.confirmBooking(id);
        return Result.success();
    }
    
    @ApiOperation("完成预约")
    @PutMapping("/{id}/complete")
    public Result<Void> completeBooking(@PathVariable Long id) {
        coachBookingService.completeBooking(id);
        return Result.success();
    }
    
    @ApiOperation("评价预约")
    @PutMapping("/{id}/rate")
    public Result<Void> rateBooking(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Integer rating = (Integer) params.get("rating");
        String comment = (String) params.get("comment");
        coachBookingService.rateBooking(id, rating, comment);
        return Result.success();
    }
}


