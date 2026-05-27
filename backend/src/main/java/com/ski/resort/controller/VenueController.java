package com.ski.resort.controller;

import com.ski.resort.common.PageResult;
import com.ski.resort.common.Result;
import com.ski.resort.entity.Venue;
import com.ski.resort.service.VenueService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Api(tags = "场地管理")
@RestController
@RequestMapping("/api/v1/venues")
public class VenueController {

    @Autowired
    private VenueService venueService;

    @ApiOperation("根据ID查询场地")
    @GetMapping("/{id}")
    public Result<Venue> getById(@PathVariable Long id) {
        return Result.success(venueService.getById(id));
    }

    @ApiOperation("新增场地")
    @PostMapping
    public Result<Boolean> save(@RequestBody Venue venue) {
        return Result.success(venueService.save(venue));
    }

    @ApiOperation("更新场地")
    @PutMapping
    public Result<Boolean> update(@RequestBody Venue venue) {
        return Result.success(venueService.update(venue));
    }

    @ApiOperation("删除场地")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(venueService.deleteById(id));
    }

    @ApiOperation("查询所有场地")
    @GetMapping("/list")
    public Result<List<Venue>> listAll() {
        return Result.success(venueService.listAll());
    }

    @ApiOperation("根据类型查询场地")
    @GetMapping("/list/type/{venueType}")
    public Result<List<Venue>> listByType(@PathVariable String venueType) {
        return Result.success(venueService.listByType(venueType));
    }

    @ApiOperation("查询可用场地")
    @GetMapping("/list/available")
    public Result<List<Venue>> listAvailable() {
        return Result.success(venueService.listAvailable());
    }

    @ApiOperation("根据难度查询场地")
    @GetMapping("/list/difficulty/{difficultyLevel}")
    public Result<List<Venue>> listByDifficulty(@PathVariable String difficultyLevel) {
        return Result.success(venueService.listByDifficulty(difficultyLevel));
    }

    @ApiOperation("分页查询场地")
    @GetMapping("/page")
    public Result<PageResult<Venue>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(venueService.page(pageNum, pageSize));
    }

    @ApiOperation("条件分页查询")
    @GetMapping("/page/condition")
    public Result<PageResult<Venue>> pageByCondition(
            @RequestParam(required = false) String venueCode,
            @RequestParam(required = false) String venueName,
            @RequestParam(required = false) String venueType,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(venueService.pageByCondition(venueCode, venueName, venueType, status, pageNum, pageSize));
    }

    @ApiOperation("更新场地状态")
    @PutMapping("/{id}/status/{status}")
    public Result<Boolean> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        return Result.success(venueService.updateStatus(id, status));
    }

    @ApiOperation("进入场地")
    @PutMapping("/{id}/enter")
    public Result<Boolean> enter(@PathVariable Long id) {
        return Result.success(venueService.enter(id));
    }

    @ApiOperation("离开场地")
    @PutMapping("/{id}/leave")
    public Result<Boolean> leave(@PathVariable Long id) {
        return Result.success(venueService.leave(id));
    }
}



