package com.ski.resort.mapper;

import com.ski.resort.entity.VenueBooking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 场地预订Mapper接口
 */
@Mapper
public interface VenueBookingMapper {
    
    /**
     * 查询所有场地预订
     */
    List<VenueBooking> selectAll();
    
    /**
     * 根据ID查询场地预订
     */
    VenueBooking selectById(Long id);
    
    /**
     * 根据用户ID查询场地预订列表
     */
    List<VenueBooking> selectByUserId(Long userId);
    
    /**
     * 根据场地ID查询预订列表
     */
    List<VenueBooking> selectByVenueId(Long venueId);
    
    /**
     * 插入场地预订
     */
    int insert(VenueBooking venueBooking);
    
    /**
     * 更新场地预订
     */
    int update(VenueBooking venueBooking);
    
    /**
     * 删除场地预订（逻辑删除）
     */
    int delete(Long id);
    
    /**
     * 更新预订状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}





