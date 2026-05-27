package com.ski.resort.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 教练预约实体类
 */
@Data
public class CoachBooking {
    
    /**
     * 预约ID
     */
    private Long id;
    
    /**
     * 预约号
     */
    private String bookingNo;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户姓名
     */
    private String userName;
    
    /**
     * 是否代他人预约：0自己，1代他人
     */
    private Integer bookingForOther;

    /**
     * 实际上课人姓名
     */
    private String studentName;

    /**
     * 实际上课人手机号
     */
    private String studentPhone;

    /**
     * 预约人数
     */
    private Integer personCount;

    /**
     * 教练ID
     */
    private Long coachId;
    
    /**
     * 教练姓名
     */
    private String coachName;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 课程类型
     */
    private String courseType;
    
    /**
     * 预约日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate bookingDate;
    
    /**
     * 开始时间
     */
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;
    
    /**
     * 结束时间
     */
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;
    
    /**
     * 预约时长（小时）
     */
    private BigDecimal hours;
    
    /**
     * 课时费单价
     */
    private BigDecimal hourlyRate;
    
    /**
     * 总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 状态：0待确认，1已确认，2进行中，3已完成，4已取消
     */
    private Integer status;
    
    /**
     * 支付状态：0未支付，1已支付
     */
    private Integer paymentStatus;
    
    /**
     * 支付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentTime;
    
    /**
     * 支付方式
     */
    private String paymentMethod;
    
    /**
     * 取消原因
     */
    private String cancelReason;
    
    /**
     * 取消时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cancelTime;
    
    /**
     * 评分（1-5）
     */
    private Integer rating;
    
    /**
     * 评价内容
     */
    private String comment;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    /**
     * 逻辑删除标志
     */
    private Integer isDeleted;
}


