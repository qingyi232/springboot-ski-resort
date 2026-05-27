package com.ski.resort.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 场地实体类
 *
 * @author XXX
 * @date 2025-10-08
 */
@Data
public class Venue implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 场地ID
     */
    private Long id;

    /**
     * 场地编号
     */
    private String venueCode;

    /**
     * 场地名称
     */
    private String venueName;

    /**
     * 类型：雪道/练习场/休息区/其他
     */
    private String venueType;

    /**
     * 难度等级（仅雪道）：初级/中级/高级/专业
     */
    private String difficultyLevel;

    /**
     * 长度（米）
     */
    private Integer length;

    /**
     * 宽度（米）
     */
    private Integer width;

    /**
     * 最大容纳人数
     */
    private Integer maxCapacity;

    /**
     * 当前人数
     */
    private Integer currentCapacity;

    /**
     * 租赁价格（元/小时）
     */
    private BigDecimal rentalPrice;

    /**
     * 状态：0维护中，1开放，2关闭
     */
    private Integer status;

    /**
     * 场地图片
     */
    private String imageUrl;

    /**
     * 场地描述
     */
    private String description;

    /**
     * 配套设施（JSON）
     */
    private String facilities;

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



