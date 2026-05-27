package com.ski.resort.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 场地预订实体类
 */
@Data
public class VenueBooking {
    
    /**
     * 预订ID
     */
    private Long id;
    
    /**
     * 预订号
     */
    private String bookingNo;
    
    /**
     * 场地ID
     */
    private Long venueId;
    
    /**
     * 场地名称
     */
    private String venueName;
    
    /**
     * 场地图片URL（从t_venue表关联查询）
     */
    private String venueImage;
    
    /**
     * 场地描述（从t_venue表关联查询）
     */
    private String venueDescription;
    
    /**
     * 场地类型（从t_venue表关联查询）
     */
    private String venueType;

    /**
     * 场地难度等级（从t_venue表关联查询）
     */
    private String venueDifficulty;
    
    /**
     * 场地容量（从t_venue表关联查询）
     */
    private Integer venueCapacity;
    
    /**
     * 预订人数
     */
    private Integer peopleCount;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户姓名
     */
    private String userName;
    
    /**
     * 预订日期
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
     * 时长（小时）
     */
    private BigDecimal hours;
    
    /**
     * 租赁单价
     */
    private BigDecimal rentalPrice;
    
    /**
     * 总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 预订状态：0待确认，1已确认，2使用中，3已完成，4已取消
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

