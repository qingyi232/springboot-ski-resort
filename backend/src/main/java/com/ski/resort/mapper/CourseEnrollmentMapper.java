package com.ski.resort.mapper;

import com.ski.resort.entity.CourseEnrollment;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 课程报名Mapper
 *
 * @author XXX
 * @date 2025-10-24
 */
@Mapper
public interface CourseEnrollmentMapper {

    /**
     * 插入课程报名
     */
    @Insert("INSERT INTO t_course_enrollment (enrollment_no, course_id, course_name, user_id, user_name, " +
            "course_price, enrollment_status, attended_lessons, remaining_lessons, " +
            "payment_status, payment_time, payment_method, create_time, update_time) " +
            "VALUES (#{enrollmentNo}, #{courseId}, #{courseName}, #{userId}, #{userName}, #{coursePrice}, " +
            "#{enrollmentStatus}, #{attendedLessons}, #{remainingLessons}, " +
            "#{paymentStatus}, #{paymentTime}, #{paymentMethod}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CourseEnrollment enrollment);

    /**
     * 根据ID查询
     */
    @Select("SELECT * FROM t_course_enrollment WHERE id = #{id} AND is_deleted = 0")
    CourseEnrollment selectById(Long id);

    /**
     * 根据报名号查询
     */
    @Select("SELECT * FROM t_course_enrollment WHERE enrollment_no = #{enrollmentNo} AND is_deleted = 0")
    CourseEnrollment selectByEnrollmentNo(String enrollmentNo);

    /**
     * 根据用户ID查询报名列表
     */
    @Select("SELECT * FROM t_course_enrollment WHERE user_id = #{userId} AND is_deleted = 0 ORDER BY create_time DESC")
    List<CourseEnrollment> selectByUserId(Long userId);

    /**
     * 根据课程ID查询报名列表
     */
    @Select("SELECT * FROM t_course_enrollment WHERE course_id = #{courseId} AND is_deleted = 0 ORDER BY create_time DESC")
    List<CourseEnrollment> selectByCourseId(Long courseId);

    /**
     * 查询所有报名（分页）
     */
    @Select("SELECT * FROM t_course_enrollment WHERE is_deleted = 0 ORDER BY create_time DESC")
    List<CourseEnrollment> selectAll();

    /**
     * 更新报名信息
     */
    @Update("UPDATE t_course_enrollment SET course_id = #{courseId}, course_name = #{courseName}, " +
            "user_id = #{userId}, user_name = #{userName}, course_price = #{coursePrice}, " +
            "enrollment_status = #{enrollmentStatus}, attended_lessons = #{attendedLessons}, " +
            "remaining_lessons = #{remainingLessons}, payment_status = #{paymentStatus}, " +
            "payment_time = #{paymentTime}, payment_method = #{paymentMethod}, update_time = NOW() " +
            "WHERE id = #{id}")
    int update(CourseEnrollment enrollment);

    /**
     * 更新报名状态
     */
    @Update("UPDATE t_course_enrollment SET enrollment_status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新支付状态
     */
    @Update("UPDATE t_course_enrollment SET payment_status = #{paymentStatus}, payment_time = #{paymentTime}, " +
            "payment_method = #{paymentMethod}, update_time = NOW() WHERE id = #{id}")
    int updatePaymentStatus(@Param("id") Long id, @Param("paymentStatus") Integer paymentStatus,
                            @Param("paymentTime") String paymentTime, @Param("paymentMethod") String paymentMethod);

    /**
     * 逻辑删除
     */
    @Update("UPDATE t_course_enrollment SET is_deleted = 1, update_time = NOW() WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 统计用户报名数量
     */
    @Select("SELECT COUNT(*) FROM t_course_enrollment WHERE user_id = #{userId} AND is_deleted = 0")
    int countByUserId(Long userId);

    /**
     * 统计课程报名数量
     */
    @Select("SELECT COUNT(*) FROM t_course_enrollment WHERE course_id = #{courseId} AND is_deleted = 0")
    int countByCourseId(Long courseId);

    /**
     * 检查用户是否已报名该课程
     */
    @Select("SELECT COUNT(*) FROM t_course_enrollment WHERE user_id = #{userId} AND course_id = #{courseId} " +
            "AND is_deleted = 0")
    int checkEnrollment(@Param("userId") Long userId, @Param("courseId") Long courseId);

    /**
     * 查询用户在指定课程的报名记录
     */
    @Select("SELECT * FROM t_course_enrollment WHERE user_id = #{userId} AND course_id = #{courseId} " +
            "AND is_deleted = 0 LIMIT 1")
    CourseEnrollment selectByUserAndCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);
}







