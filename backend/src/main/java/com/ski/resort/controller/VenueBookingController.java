package com.ski.resort.controller;

import com.ski.resort.common.Result;
import com.ski.resort.entity.VenueBooking;
import com.ski.resort.service.VenueBookingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 场地预订Controller
 */
@Api(tags = "场地预订管理")
@RestController
@RequestMapping("/api/v1/venue-bookings")
public class VenueBookingController {

    @Autowired
    private VenueBookingService venueBookingService;

    @ApiOperation("获取所有场地预订")
    @GetMapping
    public Result<Map<String, Object>> getAllBookings(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long venueId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String date) {
        
        List<VenueBooking> allBookings = venueBookingService.getAllBookings();
        
        // 根据条件筛选
        List<VenueBooking> filteredBookings = allBookings.stream()
            .filter(booking -> {
                // 按场地筛选
                if (venueId != null && !venueId.equals(booking.getVenueId())) {
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
        
        List<VenueBooking> pageBookings = startIndex < total ? 
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

    @ApiOperation("根据ID获取场地预订")
    @GetMapping("/{id}")
    public Result<VenueBooking> getBookingById(@PathVariable Long id) {
        VenueBooking booking = venueBookingService.getBookingById(id);
        return Result.success(booking);
    }

    @ApiOperation("根据用户ID获取场地预订列表")
    @GetMapping("/user/{userId}")
    public Result<List<VenueBooking>> getBookingsByUserId(@PathVariable Long userId) {
        List<VenueBooking> bookings = venueBookingService.getBookingsByUserId(userId);
        return Result.success(bookings);
    }

    @ApiOperation("根据场地ID获取预订列表")
    @GetMapping("/venue/{venueId}")
    public Result<List<VenueBooking>> getBookingsByVenueId(@PathVariable Long venueId) {
        List<VenueBooking> bookings = venueBookingService.getBookingsByVenueId(venueId);
        return Result.success(bookings);
    }

    @ApiOperation("创建场地预订")
    @PostMapping
    public Result<Long> createBooking(@RequestBody VenueBooking venueBooking) {
        Long id = venueBookingService.createBooking(venueBooking);
        return Result.success("预订成功", id);
    }

    @ApiOperation("更新场地预订")
    @PutMapping("/{id}")
    public Result<Void> updateBooking(@PathVariable Long id, @RequestBody VenueBooking venueBooking) {
        venueBooking.setId(id);
        venueBookingService.updateBooking(venueBooking);
        return Result.success();
    }

    @ApiOperation("取消场地预订")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancelBooking(@PathVariable Long id, @RequestBody(required = false) String cancelReason) {
        venueBookingService.cancelBooking(id, cancelReason);
        return Result.success();
    }

    @ApiOperation("确认场地预订")
    @PutMapping("/{id}/confirm")
    public Result<Void> confirmBooking(@PathVariable Long id) {
        venueBookingService.confirmBooking(id);
        return Result.success();
    }

    @ApiOperation("完成场地预订")
    @PutMapping("/{id}/complete")
    public Result<Void> completeBooking(@PathVariable Long id) {
        venueBookingService.completeBooking(id);
        return Result.success();
    }

    @ApiOperation("删除场地预订")
    @DeleteMapping("/{id}")
    public Result<Void> deleteBooking(@PathVariable Long id) {
        venueBookingService.deleteBooking(id);
        return Result.success();
    }
}

