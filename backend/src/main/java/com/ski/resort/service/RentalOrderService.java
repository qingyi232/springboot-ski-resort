package com.ski.resort.service;

import com.ski.resort.common.PageResult;
import com.ski.resort.entity.RentalOrder;
import java.util.List;

public interface RentalOrderService {
    RentalOrder getById(Long id);
    RentalOrder getByOrderNo(String orderNo);
    boolean save(RentalOrder rentalOrder);
    boolean update(RentalOrder rentalOrder);
    boolean deleteById(Long id);
    List<RentalOrder> listAll();
    List<RentalOrder> listByUserId(Long userId);
    List<RentalOrder> listByEquipmentId(Long equipmentId);
    List<RentalOrder> listByStatus(Integer status);
    PageResult<RentalOrder> page(Integer pageNum, Integer pageSize);
    PageResult<RentalOrder> pageByCondition(String orderNo, Long userId, Long equipmentId, Integer status, Integer pageNum, Integer pageSize);
    boolean updateStatus(Long id, Integer status);
    boolean pay(Long id, String paymentMethod);
    boolean returnEquipment(Long id);
    boolean cancel(Long id);
}



