package com.ski.resort.controller;

import com.ski.resort.common.PageResult;
import com.ski.resort.common.Result;
import com.ski.resort.entity.Coach;
import com.ski.resort.entity.CoachBooking;
import com.ski.resort.mapper.CoachBookingMapper;
import com.ski.resort.service.CoachService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 教练Controller
 *
 * @author XXX
 * @date 2025-10-08
 */
@Api(tags = "教练管理")
@RestController
@RequestMapping("/api/v1/coach")
public class CoachController {

    @Autowired
    private CoachService coachService;

    @Autowired
    private CoachBookingMapper coachBookingMapper;

    @ApiOperation("根据ID查询教练")
    @GetMapping("/{id}")
    public Result<Coach> getById(@ApiParam("教练ID") @PathVariable Long id) {
        Coach coach = coachService.getById(id);
        return Result.success(coach);
    }

    @ApiOperation("根据教练编号查询")
    @GetMapping("/code/{coachCode}")
    public Result<Coach> getByCode(@ApiParam("教练编号") @PathVariable String coachCode) {
        Coach coach = coachService.getByCode(coachCode);
        return Result.success(coach);
    }

    @ApiOperation("根据用户ID查询教练")
    @GetMapping("/user/{userId}")
    public Result<Coach> getByUserId(@ApiParam("用户ID") @PathVariable Long userId) {
        Coach coach = coachService.getByUserId(userId);
        return Result.success(coach);
    }

    @ApiOperation("新增教练")
    @PostMapping
    public Result<Boolean> save(@RequestBody Coach coach) {
        boolean result = coachService.save(coach);
        return Result.success(result);
    }

    @ApiOperation("更新教练")
    @PutMapping
    public Result<Boolean> update(@RequestBody Coach coach) {
        boolean result = coachService.update(coach);
        return Result.success(result);
    }

    @ApiOperation("删除教练")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@ApiParam("教练ID") @PathVariable Long id) {
        boolean result = coachService.deleteById(id);
        return Result.success(result);
    }

    @ApiOperation("查询所有教练")
    @GetMapping("/list")
    public Result<List<Coach>> listAll() {
        List<Coach> list = coachService.listAll();
        return Result.success(list);
    }

    @ApiOperation("根据等级查询教练列表")
    @GetMapping("/list/level/{coachLevel}")
    public Result<List<Coach>> listByLevel(@ApiParam("教练等级") @PathVariable String coachLevel) {
        List<Coach> list = coachService.listByLevel(coachLevel);
        return Result.success(list);
    }

    @ApiOperation("查询在职教练列表")
    @GetMapping("/list/active")
    public Result<List<Coach>> listActive() {
        List<Coach> list = coachService.listActive();
        return Result.success(list);
    }

    @ApiOperation("分页查询教练列表")
    @GetMapping("/page")
    public Result<PageResult<Coach>> page(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Coach> pageResult = coachService.page(pageNum, pageSize);
        return Result.success(pageResult);
    }

    @ApiOperation("根据条件分页查询")
    @GetMapping("/page/condition")
    public Result<PageResult<Coach>> pageByCondition(
            @ApiParam("教练编号") @RequestParam(required = false) String coachCode,
            @ApiParam("教练姓名") @RequestParam(required = false) String coachName,
            @ApiParam("教练等级") @RequestParam(required = false) String coachLevel,
            @ApiParam("状态") @RequestParam(required = false) Integer status,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Coach> pageResult = coachService.pageByCondition(
                coachCode, coachName, coachLevel, status, pageNum, pageSize);
        return Result.success(pageResult);
    }

    @ApiOperation("更新教练状态")
    @PutMapping("/{id}/status/{status}")
    public Result<Boolean> updateStatus(
            @ApiParam("教练ID") @PathVariable Long id,
            @ApiParam("状态") @PathVariable Integer status) {
        boolean result = coachService.updateStatus(id, status);
        return Result.success(result);
    }

    @ApiOperation("更新教练评分")
    @PutMapping("/{id}/rating")
    public Result<Boolean> updateRating(
            @ApiParam("教练ID") @PathVariable Long id,
            @ApiParam("评分") @RequestParam BigDecimal rating,
            @ApiParam("评价总数") @RequestParam Integer totalReviews) {
        boolean result = coachService.updateRating(id, rating, totalReviews);
        return Result.success(result);
    }

    @ApiOperation("增加累计学员数")
    @PutMapping("/{id}/students/{count}")
    public Result<Boolean> incrementTotalStudents(
            @ApiParam("教练ID") @PathVariable Long id,
            @ApiParam("增加数量") @PathVariable Integer count) {
        boolean result = coachService.incrementTotalStudents(id, count);
        return Result.success(result);
    }

    @ApiOperation("查询教练某日的日程安排")
    @GetMapping("/{coachId}/schedule")
    public Result<Map<String, Object>> getCoachSchedule(
            @ApiParam("教练ID") @PathVariable Long coachId,
            @ApiParam("日期 yyyy-MM-dd") @RequestParam String date) {
        Coach coach = coachService.getById(coachId);
        List<CoachBooking> bookings = coachBookingMapper.selectByCoachAndDate(coachId, date);

        int maxDailyHours = coach.getMaxDailyHours() != null ? coach.getMaxDailyHours() : 8;
        double bookedHours = bookings.stream()
                .filter(b -> b.getHours() != null)
                .mapToDouble(b -> b.getHours().doubleValue())
                .sum();

        List<Map<String, Object>> slots = bookings.stream().map(b -> {
            Map<String, Object> slot = new HashMap<>();
            slot.put("startTime", b.getStartTime() != null ? b.getStartTime().toString() : "");
            slot.put("endTime", b.getEndTime() != null ? b.getEndTime().toString() : "");
            slot.put("courseName", b.getCourseName());
            slot.put("status", b.getStatus());
            slot.put("hours", b.getHours());
            return slot;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("coachId", coachId);
        result.put("coachName", coach.getCoachName());
        result.put("date", date);
        result.put("maxDailyHours", maxDailyHours);
        result.put("bookedHours", bookedHours);
        result.put("remainingHours", maxDailyHours - bookedHours);
        result.put("bookedSlots", slots);
        return Result.success(result);
    }

}



