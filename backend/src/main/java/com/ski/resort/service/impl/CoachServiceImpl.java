package com.ski.resort.service.impl;

import com.ski.resort.common.PageResult;
import com.ski.resort.common.ResultCode;
import com.ski.resort.entity.Coach;
import com.ski.resort.exception.BusinessException;
import com.ski.resort.mapper.CoachMapper;
import com.ski.resort.service.CoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 教练Service实现类
 *
 * @author XXX
 * @date 2025-10-08
 */
@Service
public class CoachServiceImpl implements CoachService {

    @Autowired
    private CoachMapper coachMapper;

    @Override
    public Coach getById(Long id) {
        if (id == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }
        Coach coach = coachMapper.selectById(id);
        if (coach == null) {
            throw new BusinessException(ResultCode.FAILED);
        }
        return coach;
    }

    @Override
    public Coach getByCode(String coachCode) {
        if (coachCode == null || coachCode.trim().isEmpty()) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }
        return coachMapper.selectByCode(coachCode);
    }

    @Override
    public Coach getByUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }
        return coachMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public boolean save(Coach coach) {
        // 校验必填字段
        if (coach.getCoachCode() == null || coach.getCoachCode().trim().isEmpty()) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }
        if (coach.getCoachName() == null || coach.getCoachName().trim().isEmpty()) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }

        // 检查教练编号是否已存在
        Coach existingCoach = coachMapper.selectByCode(coach.getCoachCode());
        if (existingCoach != null) {
            throw new BusinessException("教练编号已存在");
        }

        // 设置默认值
        if (coach.getStatus() == null) {
            coach.setStatus(1); // 默认在职
        }
        if (coach.getRating() == null) {
            coach.setRating(BigDecimal.valueOf(5.0)); // 默认5.0分
        }
        if (coach.getTotalReviews() == null) {
            coach.setTotalReviews(0);
        }
        if (coach.getTotalStudents() == null) {
            coach.setTotalStudents(0);
        }

        int result = coachMapper.insert(coach);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean update(Coach coach) {
        if (coach.getId() == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }

        // 检查教练是否存在
        Coach existingCoach = coachMapper.selectById(coach.getId());
        if (existingCoach == null) {
            throw new BusinessException(ResultCode.FAILED);
        }

        // 如果修改了教练编号，检查新编号是否已被使用
        if (coach.getCoachCode() != null 
            && !coach.getCoachCode().equals(existingCoach.getCoachCode())) {
            Coach codeCheck = coachMapper.selectByCode(coach.getCoachCode());
            if (codeCheck != null) {
                throw new BusinessException("教练编号已被使用");
            }
        }

        int result = coachMapper.update(coach);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        if (id == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }

        Coach coach = coachMapper.selectById(id);
        if (coach == null) {
            throw new BusinessException(ResultCode.FAILED);
        }

        int result = coachMapper.deleteById(id);
        return result > 0;
    }

    @Override
    public List<Coach> listAll() {
        return coachMapper.selectAll();
    }

    @Override
    public List<Coach> listByLevel(String coachLevel) {
        if (coachLevel == null || coachLevel.trim().isEmpty()) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }
        return coachMapper.selectByLevel(coachLevel);
    }

    @Override
    public List<Coach> listActive() {
        return coachMapper.selectActive();
    }

    @Override
    public PageResult<Coach> page(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        int offset = (pageNum - 1) * pageSize;
        List<Coach> list = coachMapper.selectPage(offset, pageSize);
        Long total = coachMapper.countAll();

        PageResult<Coach> pageResult = new PageResult<>();
        pageResult.setTotal(total);
        pageResult.setPageNum(pageNum);
        pageResult.setPageSize(pageSize);
        pageResult.setTotalPages((int)((total + pageSize - 1) / pageSize));
        pageResult.setList(list);

        return pageResult;
    }

    @Override
    public PageResult<Coach> pageByCondition(String coachCode, String coachName,
                                              String coachLevel, Integer status,
                                              Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        int offset = (pageNum - 1) * pageSize;
        List<Coach> list = coachMapper.selectByCondition(coachCode, coachName, 
                                                         coachLevel, status, offset, pageSize);
        Long total = coachMapper.countByCondition(coachCode, coachName, coachLevel, status);

        PageResult<Coach> pageResult = new PageResult<>();
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

        int result = coachMapper.updateStatus(id, status);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean updateRating(Long id, BigDecimal rating, Integer totalReviews) {
        if (id == null || rating == null || totalReviews == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }

        int result = coachMapper.updateRating(id, rating, totalReviews);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean incrementTotalStudents(Long id, Integer count) {
        if (id == null || count == null || count <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }

        int result = coachMapper.incrementTotalStudents(id, count);
        return result > 0;
    }

}



