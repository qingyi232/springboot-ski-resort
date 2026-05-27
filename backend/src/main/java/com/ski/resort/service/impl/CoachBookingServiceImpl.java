package com.ski.resort.service.impl;

import com.ski.resort.entity.Coach;
import com.ski.resort.entity.CoachBooking;
import com.ski.resort.exception.BusinessException;
import com.ski.resort.mapper.CoachBookingMapper;
import com.ski.resort.mapper.CoachMapper;
import com.ski.resort.service.CoachBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 教练预约Service实现类
 */
@Service
public class CoachBookingServiceImpl implements CoachBookingService {
    
    @Autowired
    private CoachBookingMapper coachBookingMapper;
    
    @Autowired
    private CoachMapper coachMapper;
    
    @Override
    public List<CoachBooking> getAllBookings() {
        return coachBookingMapper.selectAll();
    }
    
    @Override
    public CoachBooking getBookingById(Long id) {
        CoachBooking booking = coachBookingMapper.selectById(id);
        if (booking == null) {
            throw new BusinessException("预约不存在");
        }
        return booking;
    }
    
    @Override
    public List<CoachBooking> getBookingsByUserId(Long userId) {
        return coachBookingMapper.selectByUserId(userId);
    }
    
    @Override
    public List<CoachBooking> getBookingsByCoachId(Long coachId) {
        return coachBookingMapper.selectByCoachId(coachId);
    }
    
    @Override
    public List<CoachBooking> getBookingsByCoachUserId(Long userId) {
        // 根据用户ID查找教练信息
        Coach coach = coachMapper.selectByUserId(userId);
        if (coach == null) {
            throw new BusinessException("教练信息不存在");
        }
        
        // 使用教练ID查询预约列表
        return coachBookingMapper.selectByCoachId(coach.getId());
    }
    
    @Override
    @Transactional
    public Long createBooking(CoachBooking coachBooking) {
        // 验证教练是否存在
        Coach coach = coachMapper.selectById(coachBooking.getCoachId());
        if (coach == null) {
            throw new BusinessException("教练不存在");
        }
        
        // 生成预约号
        String bookingNo = generateBookingNo();
        coachBooking.setBookingNo(bookingNo);
        
        // 设置初始状态
        if (coachBooking.getStatus() == null) {
            coachBooking.setStatus(0); // 待确认
        }
        if (coachBooking.getPaymentStatus() == null) {
            coachBooking.setPaymentStatus(0); // 未支付
        }
        
        // 设置教练信息
        coachBooking.setCoachName(coach.getCoachName());
        coachBooking.setHourlyRate(coach.getHourlyRate());
        
        // 插入预约
        coachBookingMapper.insert(coachBooking);
        
        return coachBooking.getId();
    }
    
    @Override
    @Transactional
    public void updateBooking(CoachBooking coachBooking) {
        CoachBooking existingBooking = coachBookingMapper.selectById(coachBooking.getId());
        if (existingBooking == null) {
            throw new BusinessException("预约不存在");
        }
        
        // 使用动态更新，只更新非空字段
        coachBookingMapper.updateDynamic(coachBooking);
    }
    
    @Override
    @Transactional
    public void cancelBooking(Long id, String cancelReason) {
        CoachBooking booking = coachBookingMapper.selectById(id);
        if (booking == null) {
            throw new BusinessException("预约不存在");
        }
        
        if (booking.getStatus() >= 3) {
            throw new BusinessException("该预约无法取消");
        }
        
        coachBookingMapper.cancel(id, cancelReason);
    }
    
    @Override
    @Transactional
    public void deleteBooking(Long id) {
        CoachBooking booking = coachBookingMapper.selectById(id);
        if (booking == null) {
            throw new BusinessException("预约不存在");
        }
        
        coachBookingMapper.delete(id);
    }
    
    @Override
    @Transactional
    public void confirmBooking(Long id) {
        CoachBooking booking = coachBookingMapper.selectById(id);
        if (booking == null) {
            throw new BusinessException("预约不存在");
        }
        
        if (booking.getStatus() != 0) {
            throw new BusinessException("只有待确认的预约才能确认");
        }
        
        booking.setStatus(1); // 已确认
        coachBookingMapper.updateDynamic(booking);
    }
    
    @Override
    @Transactional
    public void completeBooking(Long id) {
        CoachBooking booking = coachBookingMapper.selectById(id);
        if (booking == null) {
            throw new BusinessException("预约不存在");
        }
        
        booking.setStatus(3); // 已完成
        coachBookingMapper.updateDynamic(booking);
    }
    
    @Override
    @Transactional
    public void rateBooking(Long id, Integer rating, String comment) {
        CoachBooking booking = coachBookingMapper.selectById(id);
        if (booking == null) {
            throw new BusinessException("预约不存在");
        }
        
        if (booking.getStatus() != 3) {
            throw new BusinessException("只有已完成的预约才能评价");
        }
        
        booking.setRating(rating);
        booking.setComment(comment);
        coachBookingMapper.updateDynamic(booking);
        
        // 更新教练评分
        updateCoachRating(booking.getCoachId());
    }
    
    /**
     * 生成预约号
     */
    private String generateBookingNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "CB" + timestamp + (int)(Math.random() * 1000);
    }
    
    /**
     * 更新教练评分
     */
    private void updateCoachRating(Long coachId) {
        // TODO: 计算教练的平均评分并更新
    }
}


