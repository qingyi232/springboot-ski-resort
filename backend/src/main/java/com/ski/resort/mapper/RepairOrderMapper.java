package com.ski.resort.mapper;

import com.ski.resort.entity.RepairOrder;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RepairOrderMapper {

    @Insert("INSERT INTO t_repair_order (repair_no, user_id, user_name, equipment_id, equipment_name, " +
            "fault_description, fault_images, repair_type, priority, status, create_time, update_time) " +
            "VALUES (#{repairNo}, #{userId}, #{userName}, #{equipmentId}, #{equipmentName}, " +
            "#{faultDescription}, #{faultImages}, #{repairType}, #{priority}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RepairOrder repairOrder);

    @Select("SELECT * FROM t_repair_order WHERE id = #{id} AND is_deleted = 0")
    RepairOrder selectById(Long id);

    @Select("SELECT * FROM t_repair_order WHERE user_id = #{userId} AND is_deleted = 0 ORDER BY create_time DESC")
    List<RepairOrder> selectByUserId(Long userId);

    @Select("SELECT * FROM t_repair_order WHERE is_deleted = 0 ORDER BY priority DESC, create_time DESC")
    List<RepairOrder> selectAll();

    @Update("UPDATE t_repair_order SET status = #{status}, assigned_staff_id = #{assignedStaffId}, " +
            "assigned_staff_name = #{assignedStaffName}, start_time = NOW(), update_time = NOW() " +
            "WHERE id = #{id} AND is_deleted = 0")
    int assignStaff(@Param("id") Long id, @Param("status") Integer status,
                    @Param("assignedStaffId") Long assignedStaffId,
                    @Param("assignedStaffName") String assignedStaffName);

    @Update("UPDATE t_repair_order SET status = 2, repair_result = #{repairResult}, " +
            "repair_cost = #{repairCost}, finish_time = NOW(), update_time = NOW() " +
            "WHERE id = #{id} AND is_deleted = 0")
    int complete(@Param("id") Long id, @Param("repairResult") String repairResult,
                 @Param("repairCost") java.math.BigDecimal repairCost);

    @Update("UPDATE t_repair_order SET status = #{status}, update_time = NOW() WHERE id = #{id} AND is_deleted = 0")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("UPDATE t_repair_order SET is_deleted = 1, update_time = NOW() WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT COUNT(*) FROM t_repair_order WHERE is_deleted = 0")
    long countAll();

    @Select("SELECT COUNT(*) FROM t_repair_order WHERE status = #{status} AND is_deleted = 0")
    long countByStatus(Integer status);
}
