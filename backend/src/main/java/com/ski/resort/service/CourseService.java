package com.ski.resort.service;

import com.ski.resort.common.PageResult;
import com.ski.resort.entity.Course;
import java.util.List;

public interface CourseService {
    Course getById(Long id);
    Course getByCode(String courseCode);
    boolean save(Course course);
    boolean update(Course course);
    boolean deleteById(Long id);
    List<Course> listAll();
    List<Course> listByType(String courseType);
    List<Course> listByCoachId(Long coachId);
    List<Course> listAvailable();
    List<Course> listHot(Integer limit);
    PageResult<Course> page(Integer pageNum, Integer pageSize);
    PageResult<Course> pageByCondition(String courseCode, String courseName, String courseType, Integer status, Integer pageNum, Integer pageSize);
    boolean updateStatus(Long id, Integer status);
    boolean enroll(Long id);
    boolean cancelEnroll(Long id);
}



