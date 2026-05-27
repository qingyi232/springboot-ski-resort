package com.ski.resort.controller;

import com.ski.resort.common.Result;
import com.ski.resort.entity.RepairOrder;
import com.ski.resort.service.RepairOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Api(tags = "雪具维修管理")
@RestController
@RequestMapping("/api/v1/repairs")
@CrossOrigin
public class RepairOrderController {

    @Autowired
    private RepairOrderService repairOrderService;

    @ApiOperation("用户提交报修")
    @PostMapping
    public Result<Long> createRepair(@RequestBody RepairOrder repairOrder) {
        Long id = repairOrderService.createRepairOrder(repairOrder);
        return Result.success(id);
    }

    @ApiOperation("根据ID查询工单")
    @GetMapping("/{id}")
    public Result<RepairOrder> getById(@PathVariable Long id) {
        return Result.success(repairOrderService.getById(id));
    }

    @ApiOperation("查询用户的报修记录")
    @GetMapping("/user/{userId}")
    public Result<List<RepairOrder>> getByUserId(@PathVariable Long userId) {
        return Result.success(repairOrderService.getByUserId(userId));
    }

    @ApiOperation("查询所有维修工单（工作人员）")
    @GetMapping("/list")
    public Result<List<RepairOrder>> getAll() {
        return Result.success(repairOrderService.getAll());
    }

    @ApiOperation("指派工作人员")
    @PutMapping("/{id}/assign")
    public Result<Void> assignStaff(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Long staffId = Long.valueOf(params.get("staffId").toString());
        String staffName = (String) params.get("staffName");
        repairOrderService.assignStaff(id, staffId, staffName);
        return Result.success();
    }

    @ApiOperation("完成维修")
    @PutMapping("/{id}/complete")
    public Result<Void> completeRepair(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        String repairResult = (String) params.get("repairResult");
        BigDecimal repairCost = params.get("repairCost") != null ?
                new BigDecimal(params.get("repairCost").toString()) : BigDecimal.ZERO;
        repairOrderService.completeRepair(id, repairResult, repairCost);
        return Result.success();
    }

    @ApiOperation("更新工单状态")
    @PutMapping("/{id}/status/{status}")
    public Result<Void> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        repairOrderService.updateStatus(id, status);
        return Result.success();
    }

    @ApiOperation("删除工单")
    @DeleteMapping("/{id}")
    public Result<Void> deleteRepair(@PathVariable Long id) {
        repairOrderService.deleteById(id);
        return Result.success();
    }
}
