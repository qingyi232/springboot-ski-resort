package com.ski.resort.controller;

import com.ski.resort.common.Result;
import com.ski.resort.entity.SalesOrder;
import com.ski.resort.service.SalesOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 销售订单Controller
 */
@Api(tags = "销售订单管理")
@RestController
@RequestMapping("/api/v1/sales-orders")
@CrossOrigin
public class SalesOrderController {
    
    @Autowired
    private SalesOrderService salesOrderService;
    
    @ApiOperation("查询所有订单")
    @GetMapping
    public Result<Map<String, Object>> getAllOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String status) {
        
        List<SalesOrder> allOrders = salesOrderService.getAllOrders();
        
        // 根据条件筛选
        List<SalesOrder> filteredOrders = allOrders.stream()
            .filter(order -> {
                // 按订单号筛选
                if (orderNo != null && !orderNo.isEmpty()) {
                    if (order.getOrderNo() == null || !order.getOrderNo().contains(orderNo)) {
                        return false;
                    }
                }
                // 按状态筛选
                if (status != null && !status.isEmpty()) {
                    String orderStatus = getStatusString(order.getStatus());
                    if (!status.equals(orderStatus)) {
                        return false;
                    }
                }
                return true;
            })
            .collect(java.util.stream.Collectors.toList());
        
        // 计算分页
        int total = filteredOrders.size();
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);
        
        List<SalesOrder> pageOrders = startIndex < total ? 
            filteredOrders.subList(startIndex, endIndex) : 
            new java.util.ArrayList<>();
        
        // 返回分页结果
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("records", pageOrders);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        
        return Result.success(result);
    }
    
    // 辅助方法：将状态码转换为状态字符串
    private String getStatusString(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "pending";      // 待支付
            case 1: return "paid";         // 待发货
            case 2: return "shipped";      // 已发货
            case 3: return "completed";    // 已完成
            case 4: return "cancelled";    // 已取消
            default: return "";
        }
    }
    
    @ApiOperation("根据ID查询订单")
    @GetMapping("/{id}")
    public Result<SalesOrder> getOrderById(@PathVariable Long id) {
        SalesOrder order = salesOrderService.getOrderById(id);
        return Result.success(order);
    }
    
    @ApiOperation("根据订单号查询")
    @GetMapping("/no/{orderNo}")
    public Result<SalesOrder> getOrderByOrderNo(@PathVariable String orderNo) {
        SalesOrder order = salesOrderService.getOrderByOrderNo(orderNo);
        return Result.success(order);
    }
    
    @ApiOperation("根据用户ID查询订单列表")
    @GetMapping("/user/{userId}")
    public Result<List<SalesOrder>> getOrdersByUserId(@PathVariable Long userId) {
        List<SalesOrder> orders = salesOrderService.getOrdersByUserId(userId);
        return Result.success(orders);
    }
    
    @ApiOperation("创建订单")
    @PostMapping
    public Result<Long> createOrder(@RequestBody SalesOrder salesOrder) {
        Long id = salesOrderService.createOrder(salesOrder);
        return Result.success(id);
    }
    
    @ApiOperation("更新订单")
    @PutMapping("/{id}")
    public Result<Void> updateOrder(@PathVariable Long id, @RequestBody SalesOrder salesOrder) {
        salesOrder.setId(id);
        salesOrderService.updateOrder(salesOrder);
        return Result.success();
    }
    
    @ApiOperation("取消订单")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long id) {
        salesOrderService.cancelOrder(id);
        return Result.success();
    }
    
    @ApiOperation("删除订单")
    @DeleteMapping("/{id}")
    public Result<Void> deleteOrder(@PathVariable Long id) {
        salesOrderService.deleteOrder(id);
        return Result.success();
    }
    
    @ApiOperation("支付订单")
    @PutMapping("/{id}/pay")
    public Result<Void> payOrder(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String paymentMethod = params.get("paymentMethod");
        salesOrderService.payOrder(id, paymentMethod);
        return Result.success();
    }
    
    @ApiOperation("发货")
    @PutMapping("/{id}/ship")
    public Result<Void> shipOrder(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String expressCompany = params.get("expressCompany");
        String expressNo = params.get("expressNo");
        salesOrderService.shipOrder(id, expressCompany, expressNo);
        return Result.success();
    }
    
    @ApiOperation("确认收货")
    @PutMapping("/{id}/confirm-receipt")
    public Result<Void> confirmReceipt(@PathVariable Long id) {
        salesOrderService.confirmReceipt(id);
        return Result.success();
    }
}


