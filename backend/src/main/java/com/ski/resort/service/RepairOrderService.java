package com.ski.resort.service;

import com.ski.resort.entity.RepairOrder;
import java.math.BigDecimal;
import java.util.List;

public interface RepairOrderService {

    Long createRepairOrder(RepairOrder repairOrder);

    RepairOrder getById(Long id);

    List<RepairOrder> getByUserId(Long userId);

    List<RepairOrder> getAll();

    void assignStaff(Long id, Long staffId, String staffName);

    void completeRepair(Long id, String repairResult, BigDecimal repairCost);

    void updateStatus(Long id, Integer status);

    void deleteById(Long id);
}
