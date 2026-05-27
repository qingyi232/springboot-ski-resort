package com.ski.resort.mapper;

import com.ski.resort.entity.Coach;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 教练Mapper接口
 *
 * @author XXX
 * @date 2025-10-08
 */
@Mapper
public interface CoachMapper {

    /**
     * 根据ID查询教练
     */
    Coach selectById(@Param("id") Long id);

    /**
     * 根据教练编号查询
     */
    Coach selectByCode(@Param("coachCode") String coachCode);

    /**
     * 根据用户ID查询教练
     */
    Coach selectByUserId(@Param("userId") Long userId);

    /**
     * 插入教练
     */
    int insert(Coach coach);

    /**
     * 更新教练
     */
    int update(Coach coach);

    /**
     * 根据ID删除教练（逻辑删除）
     */
    int deleteById(@Param("id") Long id);

    /**
     * 查询所有教练
     */
    List<Coach> selectAll();

    /**
     * 根据等级查询教练列表
     */
    List<Coach> selectByLevel(@Param("coachLevel") String coachLevel);

    /**
     * 查询在职教练列表
     */
    List<Coach> selectActive();

    /**
     * 分页查询教练列表
     */
    List<Coach> selectPage(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 统计教练总数
     */
    Long countAll();

    /**
     * 根据条件统计教练数
     */
    Long countByCondition(@Param("coachCode") String coachCode,
                          @Param("coachName") String coachName,
                          @Param("coachLevel") String coachLevel,
                          @Param("status") Integer status);

    /**
     * 根据条件查询教练列表
     */
    List<Coach> selectByCondition(@Param("coachCode") String coachCode,
                                   @Param("coachName") String coachName,
                                   @Param("coachLevel") String coachLevel,
                                   @Param("status") Integer status,
                                   @Param("offset") Integer offset,
                                   @Param("limit") Integer limit);

    /**
     * 更新教练状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新教练评分
     */
    int updateRating(@Param("id") Long id, @Param("rating") java.math.BigDecimal rating, @Param("totalReviews") Integer totalReviews);

    /**
     * 增加累计学员数
     */
    int incrementTotalStudents(@Param("id") Long id, @Param("count") Integer count);

}



