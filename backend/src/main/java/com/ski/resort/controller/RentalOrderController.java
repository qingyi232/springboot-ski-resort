package com.ski.resort.controller;

import com.ski.resort.common.PageResult;
import com.ski.resort.common.Result;
import com.ski.resort.entity.RentalOrder;
import com.ski.resort.service.RentalOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Api(tags = "租赁订单管理")
@RestController
@RequestMapping("/api/v1/rental-order")
public class RentalOrderController {

    @Autowired
    private RentalOrderService rentalOrderService;

    @ApiOperation("根据ID查询订单")
    @GetMapping("/{id}")
    public Result<RentalOrder> getById(@PathVariable Long id) {
        return Result.success(rentalOrderService.getById(id));
    }

    @ApiOperation("根据订单号查询")
    @GetMapping("/orderNo/{orderNo}")
    public Result<RentalOrder> getByOrderNo(@PathVariable String orderNo) {
        return Result.success(rentalOrderService.getByOrderNo(orderNo));
    }

    @ApiOperation("新增订单")
    @PostMapping
    public Result<Boolean> save(@RequestBody RentalOrder rentalOrder) {
        return Result.success(rentalOrderService.save(rentalOrder));
    }

    @ApiOperation("更新订单")
    @PutMapping
    public Result<Boolean> update(@RequestBody RentalOrder rentalOrder) {
        return Result.success(rentalOrderService.update(rentalOrder));
    }

    @ApiOperation("删除订单")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(rentalOrderService.deleteById(id));
    }

    @ApiOperation("查询所有订单")
    @GetMapping("/list")
    public Result<List<RentalOrder>> listAll() {
        return Result.success(rentalOrderService.listAll());
    }

    @ApiOperation("根据用户ID查询订单")
    @GetMapping("/list/user/{userId}")
    public Result<List<RentalOrder>> listByUserId(@PathVariable Long userId) {
        return Result.success(rentalOrderService.listByUserId(userId));
    }

    @ApiOperation("根据雪具ID查询订单")
    @GetMapping("/list/equipment/{equipmentId}")
    public Result<List<RentalOrder>> listByEquipmentId(@PathVariable Long equipmentId) {
        return Result.success(rentalOrderService.listByEquipmentId(equipmentId));
    }

    @ApiOperation("根据状态查询订单")
    @GetMapping("/list/status/{status}")
    public Result<List<RentalOrder>> listByStatus(@PathVariable Integer status) {
        return Result.success(rentalOrderService.listByStatus(status));
    }

    @ApiOperation("分页查询订单")
    @GetMapping("/page")
    public Result<PageResult<RentalOrder>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(rentalOrderService.page(pageNum, pageSize));
    }

    @ApiOperation("条件分页查询")
    @GetMapping("/page/condition")
    public Result<PageResult<RentalOrder>> pageByCondition(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long equipmentId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(rentalOrderService.pageByCondition(orderNo, userId, equipmentId, status, pageNum, pageSize));
    }

    @ApiOperation("更新订单状态")
    @PutMapping("/{id}/status/{status}")
    public Result<Boolean> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        return Result.success(rentalOrderService.updateStatus(id, status));
    }

    @ApiOperation("支付订单")
    @PutMapping("/{id}/pay")
    public Result<Boolean> pay(@PathVariable Long id, @RequestParam String paymentMethod) {
        return Result.success(rentalOrderService.pay(id, paymentMethod));
    }

    @ApiOperation("归还设备")
    @PutMapping("/{id}/return")
    public Result<Boolean> returnEquipment(@PathVariable Long id) {
        return Result.success(rentalOrderService.returnEquipment(id));
    }

    @ApiOperation("取消订单")
    @PutMapping("/{id}/cancel")
    public Result<Boolean> cancel(@PathVariable Long id) {
        return Result.success(rentalOrderService.cancel(id));
    }
}



