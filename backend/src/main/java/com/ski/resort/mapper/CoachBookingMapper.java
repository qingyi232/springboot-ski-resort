package com.ski.resort.mapper;

import com.ski.resort.entity.CoachBooking;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 教练预约Mapper接口
 */
@Mapper
public interface CoachBookingMapper {
    
    /**
     * 查询预约列表
     */
    @Select("SELECT * FROM t_coach_booking WHERE is_deleted = 0 ORDER BY create_time DESC")
    List<CoachBooking> selectAll();
    
    /**
     * 根据ID查询预约
     */
    @Select("SELECT * FROM t_coach_booking WHERE id = #{id} AND is_deleted = 0")
    CoachBooking selectById(Long id);
    
    /**
     * 根据用户ID查询预约列表
     */
    @Select("SELECT * FROM t_coach_booking WHERE user_id = #{userId} AND is_deleted = 0 ORDER BY create_time DESC")
    List<CoachBooking> selectByUserId(Long userId);
    
    /**
     * 根据教练ID查询预约列表
     */
    @Select("SELECT * FROM t_coach_booking WHERE coach_id = #{coachId} AND is_deleted = 0 ORDER BY create_time DESC")
    List<CoachBooking> selectByCoachId(Long coachId);
    
    /**
     * 插入预约
     */
    @Insert("INSERT INTO t_coach_booking (booking_no, user_id, user_name, " +
            "booking_for_other, student_name, student_phone, person_count, " +
            "coach_id, coach_name, " +
            "course_id, course_name, course_type, booking_date, start_time, end_time, hours, hourly_rate, " +
            "total_amount, status, payment_status, remark, create_time, update_time) " +
            "VALUES (#{bookingNo}, #{userId}, #{userName}, " +
            "#{bookingForOther}, #{studentName}, #{studentPhone}, #{personCount}, " +
            "#{coachId}, #{coachName}, " +
            "#{courseId}, #{courseName}, #{courseType}, #{bookingDate}, #{startTime}, #{endTime}, #{hours}, " +
            "#{hourlyRate}, #{totalAmount}, #{status}, #{paymentStatus}, #{remark}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CoachBooking coachBooking);
    
    /**
     * 更新预约（完整更新，所有字段都会被更新）
     */
    @Update("UPDATE t_coach_booking SET course_id = #{courseId}, course_name = #{courseName}, " +
            "course_type = #{courseType}, booking_date = #{bookingDate}, start_time = #{startTime}, " +
            "end_time = #{endTime}, hours = #{hours}, total_amount = #{totalAmount}, " +
            "status = #{status}, payment_status = #{paymentStatus}, payment_time = #{paymentTime}, " +
            "payment_method = #{paymentMethod}, rating = #{rating}, comment = #{comment}, " +
            "remark = #{remark}, update_time = NOW() " +
            "WHERE id = #{id}")
    int update(CoachBooking coachBooking);
    
    /**
     * 动态更新预约（只更新非空字段，使用 XML 配置）
     */
    int updateDynamic(CoachBooking coachBooking);
    
    /**
     * 取消预约
     */
    @Update("UPDATE t_coach_booking SET status = 4, cancel_reason = #{cancelReason}, " +
            "cancel_time = NOW(), update_time = NOW() WHERE id = #{id}")
    int cancel(@Param("id") Long id, @Param("cancelReason") String cancelReason);
    
    /**
     * 删除预约（逻辑删除）
     */
    @Update("UPDATE t_coach_booking SET is_deleted = 1, update_time = NOW() WHERE id = #{id}")
    int delete(Long id);
    
    /**
     * 根据预约号查询
     */
    @Select("SELECT * FROM t_coach_booking WHERE booking_no = #{bookingNo} AND is_deleted = 0")
    CoachBooking selectByBookingNo(String bookingNo);

    /**
     * 查询教练某天的已预约时段（排除已取消状态4）
     */
    @Select("SELECT * FROM t_coach_booking WHERE coach_id = #{coachId} AND booking_date = #{bookingDate} " +
            "AND status != 4 AND is_deleted = 0 ORDER BY start_time ASC")
    List<CoachBooking> selectByCoachAndDate(@Param("coachId") Long coachId, @Param("bookingDate") String bookingDate);
}


