package com.ski.resort.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 *
 * @author XXX
 * @date 2025-10-08
 */
@Data
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    private Long id;

    /**
     * 商品编号
     */
    private String productCode;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 分类：滑雪装备/服装/配件/其他
     */
    private String category;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 型号
     */
    private String model;

    /**
     * 规格
     */
    private String specification;

    /**
     * 销售价格
     */
    private BigDecimal price;

    /**
     * 成本价格
     */
    private BigDecimal costPrice;

    /**
     * 库存数量
     */
    private Integer stockQuantity;

    /**
     * 已售数量
     */
    private Integer soldQuantity;

    /**
     * 最小库存预警值
     */
    private Integer minStock;

    /**
     * 主图URL
     */
    private String imageUrl;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 状态：0下架，1上架
     */
    private Integer status;

    /**
     * 是否热门：0否，1是
     */
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.ski.resort.config.BooleanToIntegerDeserializer.class)
    private Integer isHot;

    /**
     * 是否新品：0否，1是
     */
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.ski.resort.config.BooleanToIntegerDeserializer.class)
    private Integer isNew;

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



