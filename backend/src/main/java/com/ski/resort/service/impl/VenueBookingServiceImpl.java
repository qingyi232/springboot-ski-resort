package com.ski.resort.service.impl;

import com.ski.resort.entity.Venue;
import com.ski.resort.entity.VenueBooking;
import com.ski.resort.exception.BusinessException;
import com.ski.resort.mapper.VenueBookingMapper;
import com.ski.resort.mapper.VenueMapper;
import com.ski.resort.service.VenueBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 场地预订Service实现类
 */
@Service
public class VenueBookingServiceImpl implements VenueBookingService {

    @Autowired
    private VenueBookingMapper venueBookingMapper;

    @Autowired
    private VenueMapper venueMapper;

    @Override
    public List<VenueBooking> getAllBookings() {
        return venueBookingMapper.selectAll();
    }

    @Override
    public VenueBooking getBookingById(Long id) {
        VenueBooking booking = venueBookingMapper.selectById(id);
        if (booking == null) {
            throw new BusinessException("预订不存在");
        }
        return booking;
    }

    @Override
    public List<VenueBooking> getBookingsByUserId(Long userId) {
        return venueBookingMapper.selectByUserId(userId);
    }

    @Override
    public List<VenueBooking> getBookingsByVenueId(Long venueId) {
        return venueBookingMapper.selectByVenueId(venueId);
    }

    @Override
    @Transactional
    public Long createBooking(VenueBooking venueBooking) {
        // 验证场地是否存在
        Venue venue = venueMapper.selectById(venueBooking.getVenueId());
        if (venue == null) {
            throw new BusinessException("场地不存在");
        }

        // 检查场地容量是否足够（按预约人数判断）
        int count = (venueBooking.getPeopleCount() != null && venueBooking.getPeopleCount() > 0)
                ? venueBooking.getPeopleCount() : 1;
        if (venue.getCurrentCapacity() != null && venue.getMaxCapacity() != null
                && venue.getCurrentCapacity() + count > venue.getMaxCapacity()) {
            throw new BusinessException("该场地剩余容量不足，无法预订");
        }

        // 生成预订号
        String bookingNo = generateBookingNo();
        venueBooking.setBookingNo(bookingNo);

        // 设置初始状态
        if (venueBooking.getStatus() == null) {
            venueBooking.setStatus(0); // 待确认
        }
        if (venueBooking.getPaymentStatus() == null) {
            venueBooking.setPaymentStatus(0); // 未支付
        }

        // 设置场地信息
        venueBooking.setVenueName(venue.getVenueName());
        venueBooking.setRentalPrice(venue.getRentalPrice());

        // 插入预订
        venueBookingMapper.insert(venueBooking);

        // 场地当前人数 + 实际预约人数
        venueMapper.incrementCurrentCapacityBy(venueBooking.getVenueId(), count);

        return venueBooking.getId();
    }

    @Override
    @Transactional
    public void updateBooking(VenueBooking venueBooking) {
        VenueBooking existingBooking = getBookingById(venueBooking.getId());
        // 仅允许更新部分字段
        existingBooking.setBookingDate(venueBooking.getBookingDate());
        existingBooking.setStartTime(venueBooking.getStartTime());
        existingBooking.setEndTime(venueBooking.getEndTime());
        existingBooking.setHours(venueBooking.getHours());
        existingBooking.setTotalAmount(venueBooking.getTotalAmount());
        existingBooking.setRemark(venueBooking.getRemark());
        existingBooking.setUpdateTime(LocalDateTime.now());
        venueBookingMapper.update(existingBooking);
    }

    @Override
    @Transactional
    public void cancelBooking(Long id, String cancelReason) {
        VenueBooking booking = getBookingById(id);
        if (booking.getStatus() == 3) { // 已完成的不能取消
            throw new BusinessException("已完成的预订不能取消");
        }
        if (booking.getStatus() == 4) { // 已取消的不重复取消
            throw new BusinessException("该预订已取消");
        }
        booking.setStatus(4); // 已取消
        booking.setCancelReason(cancelReason);
        booking.setCancelTime(LocalDateTime.now());
        booking.setUpdateTime(LocalDateTime.now());
        venueBookingMapper.update(booking);

        // 场地当前人数 - 实际预约人数
        int cancelCount = (booking.getPeopleCount() != null && booking.getPeopleCount() > 0)
                ? booking.getPeopleCount() : 1;
        venueMapper.decrementCurrentCapacityBy(booking.getVenueId(), cancelCount);
    }

    @Override
    @Transactional
    public void deleteBooking(Long id) {
        venueBookingMapper.delete(id);
    }

    @Override
    @Transactional
    public void confirmBooking(Long id) {
        VenueBooking booking = getBookingById(id);
        if (booking.getStatus() != 0) {
            throw new BusinessException("只有待确认的预订才能确认");
        }
        booking.setStatus(1); // 已确认
        booking.setUpdateTime(LocalDateTime.now());
        venueBookingMapper.update(booking);
    }

    @Override
    @Transactional
    public void completeBooking(Long id) {
        VenueBooking booking = getBookingById(id);
        if (booking.getStatus() != 1 && booking.getStatus() != 2) {
            throw new BusinessException("只有已确认或使用中的预订才能完成");
        }
        booking.setStatus(3); // 已完成
        booking.setUpdateTime(LocalDateTime.now());
        venueBookingMapper.update(booking);

        // 场地当前人数 - 实际预约人数（使用结束，释放名额）
        int completeCount = (booking.getPeopleCount() != null && booking.getPeopleCount() > 0)
                ? booking.getPeopleCount() : 1;
        venueMapper.decrementCurrentCapacityBy(booking.getVenueId(), completeCount);
    }

    private String generateBookingNo() {
        return "VB" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }
}





