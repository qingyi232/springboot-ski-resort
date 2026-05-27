package com.ski.resort.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 课程实体类
 *
 * @author XXX
 * @date 2025-10-08
 */
@Data
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 课程ID
     */
    private Long id;

    /**
     * 课程编号
     */
    private String courseCode;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 类型：初级入门/中级进阶/高级提升/竞技训练
     */
    private String courseType;

    /**
     * 授课教练ID
     */
    private Long coachId;

    /**
     * 教练姓名
     */
    private String coachName;

    /**
     * 最大学员数
     */
    private Integer maxStudents;

    /**
     * 当前学员数
     */
    private Integer currentStudents;

    /**
     * 课程价格
     */
    private BigDecimal coursePrice;

    /**
     * 课程时长（小时）
     */
    private BigDecimal courseDuration;

    /**
     * 总节数
     */
    private Integer totalLessons;

    /**
     * 每节课时长（小时）
     */
    private BigDecimal perLessonHours;

    /**
     * 迟到/缺席处理规则：
     * 0-课时作废不补
     * 1-可延期补课
     * 2-迟到15分钟内可正常上课
     */
    private Integer latePolicy;

    /**
     * 开课日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate courseDate;

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
     * 场地ID
     */
    private Long venueId;

    /**
     * 场地名称
     */
    private String venueName;
    
    /**
     * 难度等级：初级/中级/高级
     */
    private String difficulty;

    /**
     * 课程描述
     */
    private String description;

    /**
     * 课程图片
     */
    private String imageUrl;

    /**
     * 状态：0未开始，1报名中，2进行中，3已结束，4已取消
     */
    private Integer status;

    /**
     * 是否热门：0否，1是
     */
    private Integer isHot;

    /**
     * 排序值
     */
    private Integer sortOrder;

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



