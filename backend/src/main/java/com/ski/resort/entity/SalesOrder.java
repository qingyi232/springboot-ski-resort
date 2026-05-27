package com.ski.resort.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 销售订单实体类
 */
@Data
public class SalesOrder {
    
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
     * 用户姓名
     */
    private String userName;
    
    /**
     * 商品数量
     */
    private Integer itemCount;
    
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;
    
    /**
     * 实付金额
     */
    private BigDecimal actualAmount;
    
    /**
     * 订单状态：0待支付，1待发货，2已发货，3已完成，4已取消
     */
    private Integer orderStatus;
    
    /**
     * 状态：0待支付，1待发货，2已发货，3已完成，4已取消
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
     * 发货状态：0未发货，1已发货，2已收货
     */
    private Integer shippingStatus;
    
    /**
     * 发货时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime shippingTime;
    
    /**
     * 收货地址
     */
    private String shippingAddress;
    
    /**
     * 收货人姓名
     */
    private String receiverName;
    
    /**
     * 收货人电话
     */
    private String receiverPhone;
    
    /**
     * 收货地址（详细地址，同shipping_address）
     */
    private String receiverAddress;
    
    /**
     * 快递公司
     */
    private String expressCompany;
    
    /**
     * 快递单号
     */
    private String expressNo;
    
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

