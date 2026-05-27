package com.ski.resort.service.impl;

import com.ski.resort.common.PageResult;
import com.ski.resort.common.ResultCode;
import com.ski.resort.entity.Equipment;
import com.ski.resort.exception.BusinessException;
import com.ski.resort.mapper.EquipmentMapper;
import com.ski.resort.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 雪具Service实现类
 *
 * @author XXX
 * @date 2025-10-08
 */
@Service
public class EquipmentServiceImpl implements EquipmentService {

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Override
    public Equipment getById(Long id) {
        if (id == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }
        Equipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new BusinessException(ResultCode.FAILED);
        }
        return equipment;
    }

    @Override
    public Equipment getByCode(String equipmentCode) {
        if (equipmentCode == null || equipmentCode.trim().isEmpty()) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }
        return equipmentMapper.selectByCode(equipmentCode);
    }

    @Override
    @Transactional
    public boolean save(Equipment equipment) {
        // 校验必填字段
        if (equipment.getEquipmentCode() == null || equipment.getEquipmentCode().trim().isEmpty()) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }
        if (equipment.getEquipmentName() == null || equipment.getEquipmentName().trim().isEmpty()) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }

        // 检查雪具编号是否已存在
        Equipment existingEquipment = equipmentMapper.selectByCode(equipment.getEquipmentCode());
        if (existingEquipment != null) {
            throw new BusinessException("雪具编号已存在");
        }

        // 设置默认值
        if (equipment.getStatus() == null) {
            equipment.setStatus(1); // 默认可用
        }
        if (equipment.getAvailableQuantity() == null) {
            equipment.setAvailableQuantity(equipment.getStockQuantity());
        }

        int result = equipmentMapper.insert(equipment);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean update(Equipment equipment) {
        if (equipment.getId() == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }

        // 检查雪具是否存在
        Equipment existingEquipment = equipmentMapper.selectById(equipment.getId());
        if (existingEquipment == null) {
            throw new BusinessException(ResultCode.FAILED);
        }

        // 如果修改了雪具编号，检查新编号是否已被使用
        if (equipment.getEquipmentCode() != null 
            && !equipment.getEquipmentCode().equals(existingEquipment.getEquipmentCode())) {
            Equipment codeCheck = equipmentMapper.selectByCode(equipment.getEquipmentCode());
            if (codeCheck != null) {
                throw new BusinessException("雪具编号已被使用");
            }
        }

        int result = equipmentMapper.update(equipment);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        if (id == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }

        Equipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new BusinessException(ResultCode.FAILED);
        }

        int result = equipmentMapper.deleteById(id);
        return result > 0;
    }

    @Override
    public List<Equipment> listAll() {
        return equipmentMapper.selectAll();
    }

    @Override
    public List<Equipment> listByType(String equipmentType) {
        if (equipmentType == null || equipmentType.trim().isEmpty()) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }
        return equipmentMapper.selectByType(equipmentType);
    }

    @Override
    public List<Equipment> listAvailable() {
        return equipmentMapper.selectAvailable();
    }

    @Override
    public PageResult<Equipment> page(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        int offset = (pageNum - 1) * pageSize;
        List<Equipment> list = equipmentMapper.selectPage(offset, pageSize);
        Long total = equipmentMapper.countAll();

        PageResult<Equipment> pageResult = new PageResult<>();
        pageResult.setTotal(total);
        pageResult.setPageNum(pageNum);
        pageResult.setPageSize(pageSize);
        pageResult.setTotalPages((int)((total + pageSize - 1) / pageSize));
        pageResult.setList(list);

        return pageResult;
    }

    @Override
    public PageResult<Equipment> pageByCondition(String equipmentCode, String equipmentName,
                                                  String equipmentType, Integer status,
                                                  Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        int offset = (pageNum - 1) * pageSize;
        List<Equipment> list = equipmentMapper.selectByCondition(equipmentCode, equipmentName, 
                                                                 equipmentType, status, offset, pageSize);
        Long total = equipmentMapper.countByCondition(equipmentCode, equipmentName, equipmentType, status);

        PageResult<Equipment> pageResult = new PageResult<>();
        pageResult.setTotal(total);
        pageResult.setPageNum(pageNum);
        pageResult.setPageSize(pageSize);
        pageResult.setTotalPages((int)((total + pageSize - 1) / pageSize));
        pageResult.setList(list);

        return pageResult;
    }

    @Override
    @Transactional
    public boolean updateStatus(Long id, Integer status) {
        if (id == null || status == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }

        int result = equipmentMapper.updateStatus(id, status);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean updateAvailableQuantity(Long id, Integer quantity) {
        if (id == null || quantity == null || quantity < 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }

        int result = equipmentMapper.updateAvailableQuantity(id, quantity);
        return result > 0;
    }

    @Override
    public List<Equipment> listLowStock(Integer threshold) {
        if (threshold == null || threshold < 0) {
            threshold = 5; // 默认阈值为5
        }
        return equipmentMapper.selectLowStock(threshold);
    }

    @Override
    @Transactional
    public boolean rentEquipment(Long id, Integer quantity) {
        if (id == null || quantity == null || quantity <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }

        Equipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new BusinessException(ResultCode.FAILED);
        }

        if (equipment.getAvailableQuantity() < quantity) {
            throw new BusinessException(ResultCode.FAILED);
        }

        int newQuantity = equipment.getAvailableQuantity() - quantity;
        int result = equipmentMapper.updateAvailableQuantity(id, newQuantity);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean returnEquipment(Long id, Integer quantity) {
        if (id == null || quantity == null || quantity <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }

        Equipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new BusinessException(ResultCode.FAILED);
        }

        int newQuantity = equipment.getAvailableQuantity() + quantity;
        // 确保不超过库存总数
        if (newQuantity > equipment.getStockQuantity()) {
            newQuantity = equipment.getStockQuantity();
        }

        int result = equipmentMapper.updateAvailableQuantity(id, newQuantity);
        return result > 0;
    }

}



