package com.ski.resort.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 雪具实体类
 *
 * @author XXX
 * @date 2025-10-08
 */
@Data
public class Equipment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 雪具ID
     */
    private Long id;

    /**
     * 雪具编号
     */
    private String equipmentCode;

    /**
     * 雪具名称
     */
    private String equipmentName;

    /**
     * 类型：单板/双板/雪杖/头盔/雪镜/雪鞋/护具
     */
    private String equipmentType;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 型号
     */
    private String model;

    /**
     * 尺寸规格
     */
    private String size;

    /**
     * 租赁价格（元/小时）
     */
    private BigDecimal rentalPrice;

    /**
     * 购入价格
     */
    private BigDecimal purchasePrice;

    /**
     * 购入日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;

    /**
     * 状态：0维修中，1可用，2已租出，3已报废
     */
    private Integer status;

    /**
     * 库存数量
     */
    private Integer stockQuantity;

    /**
     * 可用数量
     */
    private Integer availableQuantity;
    
    /**
     * 总数量（库存+租出）
     */
    private Integer totalQuantity;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 描述
     */
    private String description;

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



