package com.ski.resort.service;

import com.ski.resort.entity.VenueBooking;

import java.util.List;

/**
 * 场地预订Service接口
 */
public interface VenueBookingService {
    
    /**
     * 获取所有场地预订
     */
    List<VenueBooking> getAllBookings();
    
    /**
     * 根据ID获取场地预订
     */
    VenueBooking getBookingById(Long id);
    
    /**
     * 根据用户ID获取场地预订列表
     */
    List<VenueBooking> getBookingsByUserId(Long userId);
    
    /**
     * 根据场地ID获取预订列表
     */
    List<VenueBooking> getBookingsByVenueId(Long venueId);
    
    /**
     * 创建场地预订
     */
    Long createBooking(VenueBooking venueBooking);
    
    /**
     * 更新场地预订
     */
    void updateBooking(VenueBooking venueBooking);
    
    /**
     * 取消场地预订
     */
    void cancelBooking(Long id, String cancelReason);
    
    /**
     * 删除场地预订
     */
    void deleteBooking(Long id);
    
    /**
     * 确认场地预订
     */
    void confirmBooking(Long id);
    
    /**
     * 完成场地预订
     */
    void completeBooking(Long id);
}





