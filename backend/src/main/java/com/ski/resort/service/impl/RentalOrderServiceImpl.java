package com.ski.resort.service.impl;

import com.ski.resort.common.PageResult;
import com.ski.resort.common.ResultCode;
import com.ski.resort.entity.RentalOrder;
import com.ski.resort.exception.BusinessException;
import com.ski.resort.mapper.RentalOrderMapper;
import com.ski.resort.service.RentalOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class RentalOrderServiceImpl implements RentalOrderService {

    @Autowired
    private RentalOrderMapper rentalOrderMapper;
    
    /**
     * 生成租赁订单号
     * 格式: RO + 日期时间(yyyyMMddHHmmss) + 4位随机数
     */
    private String generateOrderNo() {
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = new Random().nextInt(9000) + 1000; // 生成1000-9999的随机数
        return "RO" + dateTime + random;
    }

    @Override
    public RentalOrder getById(Long id) {
        if (id == null) throw new BusinessException(ResultCode.VALIDATE_FAILED);
        RentalOrder order = rentalOrderMapper.selectById(id);
        if (order == null) throw new BusinessException(ResultCode.FAILED);
        return order;
    }

    @Override
    public RentalOrder getByOrderNo(String orderNo) {
        if (orderNo == null || orderNo.trim().isEmpty()) throw new BusinessException(ResultCode.VALIDATE_FAILED);
        return rentalOrderMapper.selectByOrderNo(orderNo);
    }

    @Override
    @Transactional
    public boolean save(RentalOrder rentalOrder) {
        log.info("=== 开始创建租赁订单 ===");
        log.info("订单数据: {}", rentalOrder);
        
        // 验证必要字段
        if (rentalOrder.getUserId() == null) {
            log.error("❌ 用户ID为空");
            throw new BusinessException("用户ID不能为空");
        }
        if (rentalOrder.getEquipmentId() == null) {
            log.error("❌ 雪具ID为空");
            throw new BusinessException("雪具ID不能为空");
        }
        if (rentalOrder.getRentalStartTime() == null) {
            log.error("❌ 租赁开始时间为空");
            throw new BusinessException("租赁开始时间不能为空");
        }
        
        // 自动生成订单号（如果前端没有提供）
        if (rentalOrder.getOrderNo() == null || rentalOrder.getOrderNo().trim().isEmpty()) {
            String orderNo = generateOrderNo();
            rentalOrder.setOrderNo(orderNo);
            log.info("✅ 自动生成订单号: {}", orderNo);
        } else {
            // 检查订单号是否已存在
            RentalOrder existing = rentalOrderMapper.selectByOrderNo(rentalOrder.getOrderNo());
            if (existing != null) {
                log.error("❌ 订单号已存在: {}", rentalOrder.getOrderNo());
                throw new BusinessException("订单号已存在");
            }
        }
        
        // 设置默认值
        if (rentalOrder.getOrderStatus() == null) {
            rentalOrder.setOrderStatus(0);
            log.info("设置默认订单状态: 0（待支付）");
        }
        if (rentalOrder.getPaymentStatus() == null) {
            rentalOrder.setPaymentStatus(0);
            log.info("设置默认支付状态: 0（未支付）");
        }
        
        log.info("准备插入订单，完整数据: {}", rentalOrder);
        int result = rentalOrderMapper.insert(rentalOrder);
        log.info("✅ 订单创建成功，影响行数: {}", result);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean update(RentalOrder rentalOrder) {
        if (rentalOrder.getId() == null) throw new BusinessException(ResultCode.VALIDATE_FAILED);
        RentalOrder existing = rentalOrderMapper.selectById(rentalOrder.getId());
        if (existing == null) throw new BusinessException(ResultCode.FAILED);
        return rentalOrderMapper.update(rentalOrder) > 0;
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        if (id == null) throw new BusinessException(ResultCode.VALIDATE_FAILED);
        return rentalOrderMapper.deleteById(id) > 0;
    }

    @Override
    public List<RentalOrder> listAll() {
        return rentalOrderMapper.selectAll();
    }

    @Override
    public List<RentalOrder> listByUserId(Long userId) {
        return rentalOrderMapper.selectByUserId(userId);
    }

    @Override
    public List<RentalOrder> listByEquipmentId(Long equipmentId) {
        return rentalOrderMapper.selectByEquipmentId(equipmentId);
    }

    @Override
    public List<RentalOrder> listByStatus(Integer status) {
        return rentalOrderMapper.selectByStatus(status);
    }

    @Override
    public PageResult<RentalOrder> page(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        int offset = (pageNum - 1) * pageSize;
        List<RentalOrder> list = rentalOrderMapper.selectPage(offset, pageSize);
        Long total = rentalOrderMapper.countAll();
        PageResult<RentalOrder> result = new PageResult<>();
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotalPages((int)((total + pageSize - 1) / pageSize));
        result.setList(list);
        return result;
    }

    @Override
    public PageResult<RentalOrder> pageByCondition(String orderNo, Long userId, Long equipmentId, Integer status, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        int offset = (pageNum - 1) * pageSize;
        List<RentalOrder> list = rentalOrderMapper.selectByCondition(orderNo, userId, equipmentId, status, offset, pageSize);
        Long total = rentalOrderMapper.countByCondition(orderNo, userId, equipmentId, status);
        PageResult<RentalOrder> result = new PageResult<>();
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotalPages((int)((total + pageSize - 1) / pageSize));
        result.setList(list);
        return result;
    }

    @Override
    @Transactional
    public boolean updateStatus(Long id, Integer status) {
        if (id == null || status == null) throw new BusinessException(ResultCode.VALIDATE_FAILED);
        return rentalOrderMapper.updateStatus(id, status) > 0;
    }

    @Override
    @Transactional
    public boolean pay(Long id, String paymentMethod) {
        if (id == null || paymentMethod == null) throw new BusinessException(ResultCode.VALIDATE_FAILED);
        RentalOrder order = rentalOrderMapper.selectById(id);
        if (order == null) throw new BusinessException(ResultCode.FAILED);
        order.setPaymentStatus(1);
        order.setPaymentTime(LocalDateTime.now());
        order.setPaymentMethod(paymentMethod);
        order.setOrderStatus(1);
        return rentalOrderMapper.update(order) > 0;
    }

    @Override
    @Transactional
    public boolean returnEquipment(Long id) {
        if (id == null) throw new BusinessException(ResultCode.VALIDATE_FAILED);
        RentalOrder order = rentalOrderMapper.selectById(id);
        if (order == null) throw new BusinessException(ResultCode.FAILED);
        order.setActualReturnTime(LocalDateTime.now());
        order.setOrderStatus(3);
        return rentalOrderMapper.update(order) > 0;
    }

    @Override
    @Transactional
    public boolean cancel(Long id) {
        if (id == null) throw new BusinessException(ResultCode.VALIDATE_FAILED);
        log.info("取消租赁订单，订单ID: {}", id);
        // 租赁订单的取消状态码是 3，不是 4
        // 状态码定义：0待支付，1使用中，2已归还，3已取消
        int result = rentalOrderMapper.updateStatus(id, 3);
        log.info("订单取消结果: {}", result > 0 ? "成功" : "失败");
        return result > 0;
    }
}



