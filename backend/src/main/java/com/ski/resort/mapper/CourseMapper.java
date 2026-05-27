package com.ski.resort.mapper;

import com.ski.resort.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程Mapper接口
 *
 * @author XXX
 * @date 2025-10-08
 */
@Mapper
public interface CourseMapper {

    Course selectById(@Param("id") Long id);
    Course selectByCode(@Param("courseCode") String courseCode);
    int insert(Course course);
    int update(Course course);
    int deleteById(@Param("id") Long id);
    List<Course> selectAll();
    List<Course> selectByType(@Param("courseType") String courseType);
    List<Course> selectByCoachId(@Param("coachId") Long coachId);
    List<Course> selectAvailable();
    List<Course> selectHot(@Param("limit") Integer limit);
    List<Course> selectPage(@Param("offset") Integer offset, @Param("limit") Integer limit);
    Long countAll();
    Long countByCondition(@Param("courseCode") String courseCode,
                          @Param("courseName") String courseName,
                          @Param("courseType") String courseType,
                          @Param("status") Integer status);
    List<Course> selectByCondition(@Param("courseCode") String courseCode,
                                    @Param("courseName") String courseName,
                                    @Param("courseType") String courseType,
                                    @Param("status") Integer status,
                                    @Param("offset") Integer offset,
                                    @Param("limit") Integer limit);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    int updateCurrentStudents(@Param("id") Long id, @Param("count") Integer count);
    int incrementCurrentStudents(@Param("id") Long id);
    int decrementCurrentStudents(@Param("id") Long id);

}



