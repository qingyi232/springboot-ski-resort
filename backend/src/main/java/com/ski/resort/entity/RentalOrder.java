package com.ski.resort.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 租赁订单实体类
 *
 * @author XXX
 * @date 2025-10-08
 */
@Data
public class RentalOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 雪具ID
     */
    private Long equipmentId;

    /**
     * 雪具名称
     */
    private String equipmentName;

    /**
     * 租赁开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rentalStartTime;

    /**
     * 计划归还时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rentalEndTime;

    /**
     * 实际归还时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualReturnTime;

    /**
     * 租赁时长（小时）
     */
    private BigDecimal rentalHours;

    /**
     * 租赁单价（元/小时）
     */
    private BigDecimal rentalPrice;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 押金
     */
    private BigDecimal deposit;

    /**
     * 订单状态：0待支付，1使用中，2已归还，3已取消
     */
    private Integer orderStatus;

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
     * 支付方式：alipay/wechat/cash
     */
    private String paymentMethod;

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
     * 逻辑删除：0未删除，1已删除
     */
    @JsonIgnore
    private Integer isDeleted;

}



