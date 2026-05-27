package com.ski.resort.controller;

import com.ski.resort.common.PageResult;
import com.ski.resort.common.Result;
import com.ski.resort.entity.Equipment;
import com.ski.resort.service.EquipmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 雪具Controller
 *
 * @author XXX
 * @date 2025-10-08
 */
@Api(tags = "雪具管理")
@RestController
@RequestMapping("/api/v1/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @ApiOperation("根据ID查询雪具")
    @GetMapping("/{id}")
    public Result<Equipment> getById(@ApiParam("雪具ID") @PathVariable Long id) {
        Equipment equipment = equipmentService.getById(id);
        return Result.success(equipment);
    }

    @ApiOperation("根据雪具编号查询")
    @GetMapping("/code/{equipmentCode}")
    public Result<Equipment> getByCode(@ApiParam("雪具编号") @PathVariable String equipmentCode) {
        Equipment equipment = equipmentService.getByCode(equipmentCode);
        return Result.success(equipment);
    }

    @ApiOperation("新增雪具")
    @PostMapping
    public Result<Boolean> save(@RequestBody Equipment equipment) {
        boolean result = equipmentService.save(equipment);
        return Result.success(result);
    }

    @ApiOperation("更新雪具")
    @PutMapping
    public Result<Boolean> update(@RequestBody Equipment equipment) {
        boolean result = equipmentService.update(equipment);
        return Result.success(result);
    }

    @ApiOperation("删除雪具")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@ApiParam("雪具ID") @PathVariable Long id) {
        boolean result = equipmentService.deleteById(id);
        return Result.success(result);
    }

    @ApiOperation("查询所有雪具")
    @GetMapping("/list")
    public Result<List<Equipment>> listAll() {
        List<Equipment> list = equipmentService.listAll();
        return Result.success(list);
    }

    @ApiOperation("根据类型查询雪具列表")
    @GetMapping("/list/type/{equipmentType}")
    public Result<List<Equipment>> listByType(@ApiParam("雪具类型") @PathVariable String equipmentType) {
        List<Equipment> list = equipmentService.listByType(equipmentType);
        return Result.success(list);
    }

    @ApiOperation("查询可用雪具列表")
    @GetMapping("/list/available")
    public Result<List<Equipment>> listAvailable() {
        List<Equipment> list = equipmentService.listAvailable();
        return Result.success(list);
    }

    @ApiOperation("分页查询雪具列表")
    @GetMapping("/page")
    public Result<PageResult<Equipment>> page(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Equipment> pageResult = equipmentService.page(pageNum, pageSize);
        return Result.success(pageResult);
    }

    @ApiOperation("根据条件分页查询")
    @GetMapping("/page/condition")
    public Result<PageResult<Equipment>> pageByCondition(
            @ApiParam("雪具编号") @RequestParam(required = false) String equipmentCode,
            @ApiParam("雪具名称") @RequestParam(required = false) String equipmentName,
            @ApiParam("雪具类型") @RequestParam(required = false) String equipmentType,
            @ApiParam("状态") @RequestParam(required = false) Integer status,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Equipment> pageResult = equipmentService.pageByCondition(
                equipmentCode, equipmentName, equipmentType, status, pageNum, pageSize);
        return Result.success(pageResult);
    }

    @ApiOperation("更新雪具状态")
    @PutMapping("/{id}/status/{status}")
    public Result<Boolean> updateStatus(
            @ApiParam("雪具ID") @PathVariable Long id,
            @ApiParam("状态") @PathVariable Integer status) {
        boolean result = equipmentService.updateStatus(id, status);
        return Result.success(result);
    }

    @ApiOperation("更新可用数量")
    @PutMapping("/{id}/quantity/{quantity}")
    public Result<Boolean> updateAvailableQuantity(
            @ApiParam("雪具ID") @PathVariable Long id,
            @ApiParam("可用数量") @PathVariable Integer quantity) {
        boolean result = equipmentService.updateAvailableQuantity(id, quantity);
        return Result.success(result);
    }

    @ApiOperation("查询库存不足的雪具")
    @GetMapping("/list/lowStock")
    public Result<List<Equipment>> listLowStock(
            @ApiParam("库存阈值") @RequestParam(defaultValue = "5") Integer threshold) {
        List<Equipment> list = equipmentService.listLowStock(threshold);
        return Result.success(list);
    }

    @ApiOperation("租赁雪具")
    @PutMapping("/{id}/rent/{quantity}")
    public Result<Boolean> rentEquipment(
            @ApiParam("雪具ID") @PathVariable Long id,
            @ApiParam("租赁数量") @PathVariable Integer quantity) {
        boolean result = equipmentService.rentEquipment(id, quantity);
        return Result.success(result);
    }

    @ApiOperation("归还雪具")
    @PutMapping("/{id}/return/{quantity}")
    public Result<Boolean> returnEquipment(
            @ApiParam("雪具ID") @PathVariable Long id,
            @ApiParam("归还数量") @PathVariable Integer quantity) {
        boolean result = equipmentService.returnEquipment(id, quantity);
        return Result.success(result);
    }

}



