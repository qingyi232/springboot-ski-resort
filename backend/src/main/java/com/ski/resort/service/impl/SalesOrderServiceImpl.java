package com.ski.resort.service.impl;

import com.ski.resort.entity.SalesOrder;
import com.ski.resort.exception.BusinessException;
import com.ski.resort.mapper.SalesOrderMapper;
import com.ski.resort.service.SalesOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 销售订单Service实现类
 */
@Service
public class SalesOrderServiceImpl implements SalesOrderService {
    
    @Autowired
    private SalesOrderMapper salesOrderMapper;
    
    @Override
    public List<SalesOrder> getAllOrders() {
        return salesOrderMapper.selectAll();
    }
    
    @Override
    public SalesOrder getOrderById(Long id) {
        SalesOrder order = salesOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }
    
    @Override
    public SalesOrder getOrderByOrderNo(String orderNo) {
        SalesOrder order = salesOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }
    
    @Override
    public List<SalesOrder> getOrdersByUserId(Long userId) {
        return salesOrderMapper.selectByUserId(userId);
    }
    
    @Override
    @Transactional
    public Long createOrder(SalesOrder salesOrder) {
        // 生成订单号
        String orderNo = generateOrderNo();
        salesOrder.setOrderNo(orderNo);
        
        // 设置初始状态
        if (salesOrder.getStatus() == null) {
            salesOrder.setStatus(0); // 待支付
        }
        if (salesOrder.getPaymentStatus() == null) {
            salesOrder.setPaymentStatus(0); // 未支付
        }
        if (salesOrder.getShippingStatus() == null) {
            salesOrder.setShippingStatus(0); // 未发货
        }
        
        // 计算实付金额
        if (salesOrder.getActualAmount() == null) {
            salesOrder.setActualAmount(
                salesOrder.getTotalAmount().subtract(
                    salesOrder.getDiscountAmount() != null ? 
                    salesOrder.getDiscountAmount() : java.math.BigDecimal.ZERO
                )
            );
        }
        
        // 插入订单
        salesOrderMapper.insert(salesOrder);
        
        return salesOrder.getId();
    }
    
    @Override
    @Transactional
    public void updateOrder(SalesOrder salesOrder) {
        SalesOrder existingOrder = salesOrderMapper.selectById(salesOrder.getId());
        if (existingOrder == null) {
            throw new BusinessException("订单不存在");
        }
        
        salesOrderMapper.update(salesOrder);
    }
    
    @Override
    @Transactional
    public void cancelOrder(Long id) {
        SalesOrder order = salesOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (order.getStatus() >= 2) {
            throw new BusinessException("已发货的订单无法取消");
        }
        
        salesOrderMapper.cancel(id);
    }
    
    @Override
    @Transactional
    public void deleteOrder(Long id) {
        SalesOrder order = salesOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        salesOrderMapper.delete(id);
    }
    
    @Override
    @Transactional
    public void payOrder(Long id, String paymentMethod) {
        SalesOrder order = salesOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (order.getPaymentStatus() == 1) {
            throw new BusinessException("订单已支付");
        }
        
        order.setPaymentStatus(1);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentTime(LocalDateTime.now());
        order.setStatus(1); // 待发货
        
        salesOrderMapper.update(order);
    }
    
    @Override
    @Transactional
    public void shipOrder(Long id, String expressCompany, String expressNo) {
        SalesOrder order = salesOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (order.getPaymentStatus() != 1) {
            throw new BusinessException("订单未支付，无法发货");
        }
        
        order.setShippingStatus(1); // 已发货
        order.setShippingTime(LocalDateTime.now());
        order.setExpressCompany(expressCompany);
        order.setExpressNo(expressNo);
        order.setStatus(2); // 已发货
        
        salesOrderMapper.update(order);
    }
    
    @Override
    @Transactional
    public void confirmReceipt(Long id) {
        SalesOrder order = salesOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (order.getShippingStatus() != 1) {
            throw new BusinessException("订单未发货，无法确认收货");
        }
        
        order.setShippingStatus(2); // 已收货
        order.setStatus(3); // 已完成
        
        salesOrderMapper.update(order);
    }
    
    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "SO" + timestamp + (int)(Math.random() * 1000);
    }
}







