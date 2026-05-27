package com.ski.resort.mapper;

import com.ski.resort.entity.SalesOrder;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 销售订单Mapper接口
 */
@Mapper
public interface SalesOrderMapper {
    
    /**
     * 查询订单列表
     */
    @Select("SELECT * FROM t_sales_order WHERE is_deleted = 0 ORDER BY create_time DESC")
    List<SalesOrder> selectAll();
    
    /**
     * 根据ID查询订单
     */
    @Select("SELECT * FROM t_sales_order WHERE id = #{id} AND is_deleted = 0")
    SalesOrder selectById(Long id);
    
    /**
     * 根据订单号查询
     */
    @Select("SELECT * FROM t_sales_order WHERE order_no = #{orderNo} AND is_deleted = 0")
    SalesOrder selectByOrderNo(String orderNo);
    
    /**
     * 根据用户ID查询订单列表
     */
    @Select("SELECT * FROM t_sales_order WHERE user_id = #{userId} AND is_deleted = 0 ORDER BY create_time DESC")
    List<SalesOrder> selectByUserId(Long userId);
    
    /**
     * 插入订单
     */
    @Insert("INSERT INTO t_sales_order (order_no, user_id, user_name, item_count, total_amount, " +
            "discount_amount, actual_amount, status, payment_status, shipping_address, " +
            "receiver_name, receiver_phone, receiver_address, remark, create_time, update_time) " +
            "VALUES (#{orderNo}, #{userId}, #{userName}, #{itemCount}, #{totalAmount}, #{discountAmount}, " +
            "#{actualAmount}, #{status}, #{paymentStatus}, #{shippingAddress}, #{receiverName}, " +
            "#{receiverPhone}, #{receiverAddress}, #{remark}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SalesOrder salesOrder);
    
    /**
     * 更新订单
     */
    @Update("UPDATE t_sales_order SET status = #{status}, payment_status = #{paymentStatus}, " +
            "payment_time = #{paymentTime}, payment_method = #{paymentMethod}, " +
            "shipping_status = #{shippingStatus}, shipping_time = #{shippingTime}, " +
            "express_company = #{expressCompany}, express_no = #{expressNo}, " +
            "remark = #{remark}, update_time = NOW() WHERE id = #{id}")
    int update(SalesOrder salesOrder);
    
    /**
     * 取消订单
     */
    @Update("UPDATE t_sales_order SET status = 4, update_time = NOW() WHERE id = #{id}")
    int cancel(Long id);
    
    /**
     * 删除订单（逻辑删除）
     */
    @Update("UPDATE t_sales_order SET is_deleted = 1, update_time = NOW() WHERE id = #{id}")
    int delete(Long id);
}


