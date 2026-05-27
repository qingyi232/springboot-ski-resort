package com.ski.resort.controller;

import com.ski.resort.common.PageResult;
import com.ski.resort.common.Result;
import com.ski.resort.entity.Coach;
import com.ski.resort.entity.Course;
import com.ski.resort.service.CoachService;
import com.ski.resort.service.CourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Api(tags = "课程管理")
@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;
    
    @Autowired
    private CoachService coachService;

    @ApiOperation("根据ID查询课程")
    @GetMapping("/{id}")
    public Result<Course> getById(@ApiParam("课程ID") @PathVariable Long id) {
        return Result.success(courseService.getById(id));
    }

    @ApiOperation("新增课程")
    @PostMapping
    public Result<Boolean> save(@RequestBody Course course) {
        return Result.success(courseService.save(course));
    }

    @ApiOperation("更新课程")
    @PutMapping
    public Result<Boolean> update(@RequestBody Course course) {
        return Result.success(courseService.update(course));
    }

    @ApiOperation("删除课程")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(courseService.deleteById(id));
    }

    @ApiOperation("查询所有课程")
    @GetMapping("/list")
    public Result<List<Course>> listAll() {
        return Result.success(courseService.listAll());
    }

    @ApiOperation("根据类型查询课程")
    @GetMapping("/list/type/{courseType}")
    public Result<List<Course>> listByType(@PathVariable String courseType) {
        return Result.success(courseService.listByType(courseType));
    }

    @ApiOperation("根据教练ID查询课程")
    @GetMapping("/list/coach/{coachId}")
    public Result<List<Course>> listByCoachId(@PathVariable Long coachId) {
        return Result.success(courseService.listByCoachId(coachId));
    }
    
    @ApiOperation("根据用户ID获取教练的课程列表")
    @GetMapping("/coach/{userId}")
    public Result<List<Course>> getCoursesByCoachId(@PathVariable Long userId) {
        // 通过用户ID获取教练信息
        Coach coach = coachService.getByUserId(userId);
        if (coach == null) {
            return Result.error("教练信息不存在");
        }
        // 通过教练ID查询课程列表
        return Result.success(courseService.listByCoachId(coach.getId()));
    }

    @ApiOperation("查询可报名课程")
    @GetMapping("/list/available")
    public Result<List<Course>> listAvailable() {
        return Result.success(courseService.listAvailable());
    }

    @ApiOperation("查询热门课程")
    @GetMapping("/list/hot")
    public Result<List<Course>> listHot(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(courseService.listHot(limit));
    }

    @ApiOperation("分页查询课程")
    @GetMapping("/page")
    public Result<PageResult<Course>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(courseService.page(pageNum, pageSize));
    }

    @ApiOperation("条件分页查询")
    @GetMapping("/page/condition")
    public Result<PageResult<Course>> pageByCondition(
            @RequestParam(required = false) String courseCode,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) String courseType,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(courseService.pageByCondition(courseCode, courseName, courseType, status, pageNum, pageSize));
    }

    @ApiOperation("更新课程状态")
    @PutMapping("/{id}/status/{status}")
    public Result<Boolean> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        return Result.success(courseService.updateStatus(id, status));
    }

    @ApiOperation("报名课程")
    @PutMapping("/{id}/enroll")
    public Result<Boolean> enroll(@PathVariable Long id) {
        return Result.success(courseService.enroll(id));
    }

    @ApiOperation("取消报名")
    @PutMapping("/{id}/cancel")
    public Result<Boolean> cancelEnroll(@PathVariable Long id) {
        return Result.success(courseService.cancelEnroll(id));
    }
}



