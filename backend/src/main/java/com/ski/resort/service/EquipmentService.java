package com.ski.resort.service;

import com.ski.resort.common.PageResult;
import com.ski.resort.entity.Equipment;

import java.util.List;

/**
 * 雪具Service接口
 *
 * @author XXX
 * @date 2025-10-08
 */
public interface EquipmentService {

    /**
     * 根据ID查询雪具
     */
    Equipment getById(Long id);

    /**
     * 根据雪具编号查询
     */
    Equipment getByCode(String equipmentCode);

    /**
     * 新增雪具
     */
    boolean save(Equipment equipment);

    /**
     * 更新雪具
     */
    boolean update(Equipment equipment);

    /**
     * 删除雪具
     */
    boolean deleteById(Long id);

    /**
     * 查询所有雪具
     */
    List<Equipment> listAll();

    /**
     * 根据类型查询雪具列表
     */
    List<Equipment> listByType(String equipmentType);

    /**
     * 查询可用雪具列表
     */
    List<Equipment> listAvailable();

    /**
     * 分页查询雪具列表
     */
    PageResult<Equipment> page(Integer pageNum, Integer pageSize);

    /**
     * 根据条件分页查询
     */
    PageResult<Equipment> pageByCondition(String equipmentCode, String equipmentName, 
                                          String equipmentType, Integer status,
                                          Integer pageNum, Integer pageSize);

    /**
     * 更新雪具状态
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 更新可用数量
     */
    boolean updateAvailableQuantity(Long id, Integer quantity);

    /**
     * 查询库存不足的雪具
     */
    List<Equipment> listLowStock(Integer threshold);

    /**
     * 租赁雪具（减少可用数量）
     */
    boolean rentEquipment(Long id, Integer quantity);

    /**
     * 归还雪具（增加可用数量）
     */
    boolean returnEquipment(Long id, Integer quantity);

}



