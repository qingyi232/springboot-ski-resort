package com.ski.resort.service;

import com.ski.resort.entity.SalesOrder;

import java.util.List;

/**
 * 销售订单Service接口
 */
public interface SalesOrderService {
    
    /**
     * 查询订单列表
     */
    List<SalesOrder> getAllOrders();
    
    /**
     * 根据ID查询订单
     */
    SalesOrder getOrderById(Long id);
    
    /**
     * 根据订单号查询
     */
    SalesOrder getOrderByOrderNo(String orderNo);
    
    /**
     * 根据用户ID查询订单列表
     */
    List<SalesOrder> getOrdersByUserId(Long userId);
    
    /**
     * 创建订单
     */
    Long createOrder(SalesOrder salesOrder);
    
    /**
     * 更新订单
     */
    void updateOrder(SalesOrder salesOrder);
    
    /**
     * 取消订单
     */
    void cancelOrder(Long id);
    
    /**
     * 删除订单
     */
    void deleteOrder(Long id);
    
    /**
     * 支付订单
     */
    void payOrder(Long id, String paymentMethod);
    
    /**
     * 发货
     */
    void shipOrder(Long id, String expressCompany, String expressNo);
    
    /**
     * 确认收货
     */
    void confirmReceipt(Long id);
}







