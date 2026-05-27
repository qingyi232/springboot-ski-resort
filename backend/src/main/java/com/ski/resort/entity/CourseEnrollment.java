package com.ski.resort.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程报名实体类
 *
 * @author XXX
 * @date 2025-10-24
 */
@Data
public class CourseEnrollment {

    /**
     * 报名ID
     */
    private Long id;

    /**
     * 报名号
     */
    private String enrollmentNo;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 课程价格
     */
    private BigDecimal coursePrice;

    /**
     * 报名状态：0待审核，1已通过，2学习中，3已完成
     */
    private Integer enrollmentStatus;

    /**
     * 已上课节数
     */
    private Integer attendedLessons;

    /**
     * 剩余课节数
     */
    private Integer remainingLessons;

    /**
     * 支付状态：0未支付，1已支付
     */
    private Integer paymentStatus;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除：0未删除，1已删除
     */
    private Integer isDeleted;
}







