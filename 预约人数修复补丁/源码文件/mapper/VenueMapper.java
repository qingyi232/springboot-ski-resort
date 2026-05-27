package com.ski.resort.mapper;

import com.ski.resort.entity.Venue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 场地Mapper接口
 *
 * @author XXX
 * @date 2025-10-08
 */
@Mapper
public interface VenueMapper {

    Venue selectById(@Param("id") Long id);
    Venue selectByCode(@Param("venueCode") String venueCode);
    int insert(Venue venue);
    int update(Venue venue);
    int deleteById(@Param("id") Long id);
    List<Venue> selectAll();
    List<Venue> selectByType(@Param("venueType") String venueType);
    List<Venue> selectAvailable();
    List<Venue> selectByDifficulty(@Param("difficultyLevel") String difficultyLevel);
    List<Venue> selectPage(@Param("offset") Integer offset, @Param("limit") Integer limit);
    Long countAll();
    Long countByCondition(@Param("venueCode") String venueCode,
                          @Param("venueName") String venueName,
                          @Param("venueType") String venueType,
                          @Param("status") Integer status);
    List<Venue> selectByCondition(@Param("venueCode") String venueCode,
                                   @Param("venueName") String venueName,
                                   @Param("venueType") String venueType,
                                   @Param("status") Integer status,
                                   @Param("offset") Integer offset,
                                   @Param("limit") Integer limit);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    int updateCurrentCapacity(@Param("id") Long id, @Param("count") Integer count);
    int incrementCurrentCapacity(@Param("id") Long id);
    int decrementCurrentCapacity(@Param("id") Long id);
    int incrementCurrentCapacityBy(@Param("id") Long id, @Param("count") Integer count);
    int decrementCurrentCapacityBy(@Param("id") Long id, @Param("count") Integer count);

}



