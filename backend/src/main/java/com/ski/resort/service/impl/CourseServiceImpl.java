package com.ski.resort.service.impl;

import com.ski.resort.common.PageResult;
import com.ski.resort.common.ResultCode;
import com.ski.resort.entity.Course;
import com.ski.resort.exception.BusinessException;
import com.ski.resort.mapper.CourseMapper;
import com.ski.resort.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public Course getById(Long id) {
        if (id == null) throw new BusinessException(ResultCode.VALIDATE_FAILED);
        Course course = courseMapper.selectById(id);
        if (course == null) throw new BusinessException(ResultCode.FAILED);
        return course;
    }

    @Override
    public Course getByCode(String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) throw new BusinessException(ResultCode.VALIDATE_FAILED);
        return courseMapper.selectByCode(courseCode);
    }

    @Override
    @Transactional
    public boolean save(Course course) {
        if (course.getCourseCode() == null || course.getCourseCode().trim().isEmpty()) throw new BusinessException(ResultCode.VALIDATE_FAILED);
        if (course.getCourseName() == null || course.getCourseName().trim().isEmpty()) throw new BusinessException(ResultCode.VALIDATE_FAILED);
        Course existing = courseMapper.selectByCode(course.getCourseCode());
        if (existing != null) throw new BusinessException("课程编号已存在");
        if (course.getStatus() == null) course.setStatus(1);
        if (course.getCurrentStudents() == null) course.setCurrentStudents(0);
        if (course.getIsHot() == null) course.setIsHot(0);
        if (course.getSortOrder() == null) course.setSortOrder(0);
        
        // 设置默认开始和结束时间，避免数据库NOT NULL错误
        if (course.getStartTime() == null) {
            course.setStartTime(java.time.LocalTime.of(9, 0)); // 默认上午9点
        }
        if (course.getEndTime() == null) {
            course.setEndTime(java.time.LocalTime.of(17, 0)); // 默认下午5点
        }
        
        return courseMapper.insert(course) > 0;
    }

    @Override
    @Transactional
    public boolean update(Course course) {
        if (course.getId() == null) throw new BusinessException(ResultCode.VALIDATE_FAILED);
        Course existing = courseMapper.selectById(course.getId());
        if (existing == null) throw new BusinessException(ResultCode.FAILED);
        return courseMapper.update(course) > 0;
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        if (id == null) throw new BusinessException(ResultCode.VALIDATE_FAILED);
        return courseMapper.deleteById(id) > 0;
    }

    @Override
    public List<Course> listAll() {
        return courseMapper.selectAll();
    }

    @Override
    public List<Course> listByType(String courseType) {
        return courseMapper.selectByType(courseType);
    }

    @Override
    public List<Course> listByCoachId(Long coachId) {
        return courseMapper.selectByCoachId(coachId);
    }

    @Override
    public List<Course> listAvailable() {
        return courseMapper.selectAvailable();
    }

    @Override
    public List<Course> listHot(Integer limit) {
        if (limit == null || limit <= 0) limit = 10;
        return courseMapper.selectHot(limit);
    }

    @Override
    public PageResult<Course> page(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        int offset = (pageNum - 1) * pageSize;
        List<Course> list = courseMapper.selectPage(offset, pageSize);
        Long total = courseMapper.countAll();
        PageResult<Course> result = new PageResult<>();
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotalPages((int)((total + pageSize - 1) / pageSize));
        result.setList(list);
        return result;
    }

    @Override
    public PageResult<Course> pageByCondition(String courseCode, String courseName, String courseType, Integer status, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        int offset = (pageNum - 1) * pageSize;
        List<Course> list = courseMapper.selectByCondition(courseCode, courseName, courseType, status, offset, pageSize);
        Long total = courseMapper.countByCondition(courseCode, courseName, courseType, status);
        PageResult<Course> result = new PageResult<>();
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotalPages((int)((total + pageSize - 1) / pageSize));
        result.setList(list);
        return result;
    }

    @Override
    @Transactional
    public boolean updateStatus(Long id, Integer status) {
        if (id == null || status == null) throw new BusinessException(ResultCode.VALIDATE_FAILED);
        return courseMapper.updateStatus(id, status) > 0;
    }

    @Override
    @Transactional
    public boolean enroll(Long id) {
        if (id == null) throw new BusinessException(ResultCode.VALIDATE_FAILED);
        Course course = courseMapper.selectById(id);
        if (course == null) throw new BusinessException(ResultCode.FAILED);
        if (course.getCurrentStudents() >= course.getMaxStudents()) throw new BusinessException(ResultCode.FAILED);
        return courseMapper.incrementCurrentStudents(id) > 0;
    }

    @Override
    @Transactional
    public boolean cancelEnroll(Long id) {
        if (id == null) throw new BusinessException(ResultCode.VALIDATE_FAILED);
        return courseMapper.decrementCurrentStudents(id) > 0;
    }
}



