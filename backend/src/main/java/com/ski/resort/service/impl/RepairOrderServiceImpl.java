package com.ski.resort.service.impl;

import com.ski.resort.entity.RepairOrder;
import com.ski.resort.exception.BusinessException;
import com.ski.resort.mapper.RepairOrderMapper;
import com.ski.resort.service.RepairOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class RepairOrderServiceImpl implements RepairOrderService {

    @Autowired
    private RepairOrderMapper repairOrderMapper;

    @Override
    @Transactional
    public Long createRepairOrder(RepairOrder repairOrder) {
        if (repairOrder.getEquipmentId() == null) {
            throw new BusinessException("雪具ID不能为空");
        }
        if (repairOrder.getFaultDescription() == null || repairOrder.getFaultDescription().trim().isEmpty()) {
            throw new BusinessException("故障描述不能为空");
        }
        String repairNo = "RP" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
        repairOrder.setRepairNo(repairNo);
        if (repairOrder.getStatus() == null) repairOrder.setStatus(0);
        if (repairOrder.getPriority() == null) repairOrder.setPriority(1);
        if (repairOrder.getRepairType() == null) repairOrder.setRepairType("普通维修");
        repairOrderMapper.insert(repairOrder);
        return repairOrder.getId();
    }

    @Override
    public RepairOrder getById(Long id) {
        RepairOrder order = repairOrderMapper.selectById(id);
        if (order == null) throw new BusinessException("维修工单不存在");
        return order;
    }

    @Override
    public List<RepairOrder> getByUserId(Long userId) {
        return repairOrderMapper.selectByUserId(userId);
    }

    @Override
    public List<RepairOrder> getAll() {
        return repairOrderMapper.selectAll();
    }

    @Override
    @Transactional
    public void assignStaff(Long id, Long staffId, String staffName) {
        repairOrderMapper.assignStaff(id, 1, staffId, staffName);
    }

    @Override
    @Transactional
    public void completeRepair(Long id, String repairResult, BigDecimal repairCost) {
        repairOrderMapper.complete(id, repairResult, repairCost != null ? repairCost : BigDecimal.ZERO);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        repairOrderMapper.updateStatus(id, status);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repairOrderMapper.deleteById(id);
    }
}
