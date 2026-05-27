package com.ski.resort.service;

import com.ski.resort.common.PageResult;
import com.ski.resort.entity.Coach;

import java.math.BigDecimal;
import java.util.List;

/**
 * 教练Service接口
 *
 * @author XXX
 * @date 2025-10-08
 */
public interface CoachService {

    /**
     * 根据ID查询教练
     */
    Coach getById(Long id);

    /**
     * 根据教练编号查询
     */
    Coach getByCode(String coachCode);

    /**
     * 根据用户ID查询教练
     */
    Coach getByUserId(Long userId);

    /**
     * 新增教练
     */
    boolean save(Coach coach);

    /**
     * 更新教练
     */
    boolean update(Coach coach);

    /**
     * 删除教练
     */
    boolean deleteById(Long id);

    /**
     * 查询所有教练
     */
    List<Coach> listAll();

    /**
     * 根据等级查询教练列表
     */
    List<Coach> listByLevel(String coachLevel);

    /**
     * 查询在职教练列表
     */
    List<Coach> listActive();

    /**
     * 分页查询教练列表
     */
    PageResult<Coach> page(Integer pageNum, Integer pageSize);

    /**
     * 根据条件分页查询
     */
    PageResult<Coach> pageByCondition(String coachCode, String coachName,
                                      String coachLevel, Integer status,
                                      Integer pageNum, Integer pageSize);

    /**
     * 更新教练状态
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 更新教练评分
     */
    boolean updateRating(Long id, BigDecimal rating, Integer totalReviews);

    /**
     * 增加累计学员数
     */
    boolean incrementTotalStudents(Long id, Integer count);

}



