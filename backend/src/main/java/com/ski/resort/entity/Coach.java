package com.ski.resort.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 教练实体类
 *
 * @author XXX
 * @date 2025-10-08
 */
@Data
public class Coach implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 教练ID
     */
    private Long id;

    /**
     * 关联用户ID
     */
    private Long userId;

    /**
     * 教练姓名
     */
    private String coachName;

    /**
     * 教练编号
     */
    private String coachCode;

    /**
     * 等级：初级/中级/高级/专业
     */
    private String coachLevel;

    /**
     * 专长
     */
    private String specialty;

    /**
     * 从业年限
     */
    private Integer experienceYears;

    /**
     * 认证信息
     */
    private String certification;

    /**
     * 课时费（元/小时）
     */
    private BigDecimal hourlyRate;

    /**
     * 评分（1-5）
     */
    private BigDecimal rating;

    /**
     * 评价总数
     */
    private Integer totalReviews;

    /**
     * 累计学员数
     */
    private Integer totalStudents;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 个人简介
     */
    private String introduction;

    /**
     * 每日最大授课时长（小时）
     */
    private Integer maxDailyHours;

    /**
     * 状态：0休假，1在职，2离职
     */
    private Integer status;

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



