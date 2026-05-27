package com.ski.resort.service;

import com.ski.resort.common.PageResult;
import com.ski.resort.entity.CoachBooking;

import java.util.List;

/**
 * 教练预约Service接口
 */
public interface CoachBookingService {
    
    /**
     * 查询预约列表
     */
    List<CoachBooking> getAllBookings();
    
    /**
     * 根据ID查询预约
     */
    CoachBooking getBookingById(Long id);
    
    /**
     * 根据用户ID查询预约列表
     */
    List<CoachBooking> getBookingsByUserId(Long userId);
    
    /**
     * 根据教练ID查询预约列表
     */
    List<CoachBooking> getBookingsByCoachId(Long coachId);
    
    /**
     * 根据教练用户ID查询预约列表（自动转换为教练ID）
     */
    List<CoachBooking> getBookingsByCoachUserId(Long userId);
    
    /**
     * 创建预约
     */
    Long createBooking(CoachBooking coachBooking);
    
    /**
     * 更新预约
     */
    void updateBooking(CoachBooking coachBooking);
    
    /**
     * 取消预约
     */
    void cancelBooking(Long id, String cancelReason);
    
    /**
     * 删除预约
     */
    void deleteBooking(Long id);
    
    /**
     * 确认预约
     */
    void confirmBooking(Long id);
    
    /**
     * 完成预约
     */
    void completeBooking(Long id);
    
    /**
     * 评价预约
     */
    void rateBooking(Long id, Integer rating, String comment);
}


