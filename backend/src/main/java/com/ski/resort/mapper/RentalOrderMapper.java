package com.ski.resort.mapper;

import com.ski.resort.entity.RentalOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 租赁订单Mapper接口
 *
 * @author XXX
 * @date 2025-10-08
 */
@Mapper
public interface RentalOrderMapper {

    RentalOrder selectById(@Param("id") Long id);
    RentalOrder selectByOrderNo(@Param("orderNo") String orderNo);
    int insert(RentalOrder rentalOrder);
    int update(RentalOrder rentalOrder);
    int deleteById(@Param("id") Long id);
    List<RentalOrder> selectAll();
    List<RentalOrder> selectByUserId(@Param("userId") Long userId);
    List<RentalOrder> selectByEquipmentId(@Param("equipmentId") Long equipmentId);
    List<RentalOrder> selectByStatus(@Param("status") Integer status);
    List<RentalOrder> selectPage(@Param("offset") Integer offset, @Param("limit") Integer limit);
    Long countAll();
    Long countByCondition(@Param("orderNo") String orderNo,
                          @Param("userId") Long userId,
                          @Param("equipmentId") Long equipmentId,
                          @Param("status") Integer status);
    List<RentalOrder> selectByCondition(@Param("orderNo") String orderNo,
                                         @Param("userId") Long userId,
                                         @Param("equipmentId") Long equipmentId,
                                         @Param("status") Integer status,
                                         @Param("offset") Integer offset,
                                         @Param("limit") Integer limit);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    int updatePaymentStatus(@Param("id") Long id, @Param("paymentStatus") Integer paymentStatus);

}



