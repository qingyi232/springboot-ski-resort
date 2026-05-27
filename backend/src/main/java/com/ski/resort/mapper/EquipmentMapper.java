package com.ski.resort.mapper;

import com.ski.resort.entity.Equipment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 雪具Mapper接口
 *
 * @author XXX
 * @date 2025-10-08
 */
@Mapper
public interface EquipmentMapper {

    /**
     * 根据ID查询雪具
     */
    Equipment selectById(@Param("id") Long id);

    /**
     * 根据雪具编号查询
     */
    Equipment selectByCode(@Param("equipmentCode") String equipmentCode);

    /**
     * 插入雪具
     */
    int insert(Equipment equipment);

    /**
     * 更新雪具
     */
    int update(Equipment equipment);

    /**
     * 根据ID删除雪具（逻辑删除）
     */
    int deleteById(@Param("id") Long id);

    /**
     * 查询所有雪具
     */
    List<Equipment> selectAll();

    /**
     * 根据类型查询雪具列表
     */
    List<Equipment> selectByType(@Param("equipmentType") String equipmentType);

    /**
     * 查询可用雪具列表
     */
    List<Equipment> selectAvailable();

    /**
     * 分页查询雪具列表
     */
    List<Equipment> selectPage(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 统计雪具总数
     */
    Long countAll();

    /**
     * 根据条件统计雪具数
     */
    Long countByCondition(@Param("equipmentCode") String equipmentCode,
                          @Param("equipmentName") String equipmentName,
                          @Param("equipmentType") String equipmentType,
                          @Param("status") Integer status);

    /**
     * 根据条件查询雪具列表
     */
    List<Equipment> selectByCondition(@Param("equipmentCode") String equipmentCode,
                                       @Param("equipmentName") String equipmentName,
                                       @Param("equipmentType") String equipmentType,
                                       @Param("status") Integer status,
                                       @Param("offset") Integer offset,
                                       @Param("limit") Integer limit);

    /**
     * 更新雪具状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新可用数量
     */
    int updateAvailableQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 查询库存不足的雪具
     */
    List<Equipment> selectLowStock(@Param("threshold") Integer threshold);

}



