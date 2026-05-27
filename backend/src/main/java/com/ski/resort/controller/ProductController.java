package com.ski.resort.controller;

import com.ski.resort.common.PageResult;
import com.ski.resort.common.Result;
import com.ski.resort.entity.Product;
import com.ski.resort.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品Controller
 *
 * @author XXX
 * @date 2025-10-08
 */
@Slf4j
@Api(tags = "商品管理")
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @ApiOperation("根据ID查询商品")
    @GetMapping("/{id}")
    public Result<Product> getById(@ApiParam("商品ID") @PathVariable Long id) {
        Product product = productService.getById(id);
        return Result.success(product);
    }

    @ApiOperation("根据商品编号查询")
    @GetMapping("/code/{productCode}")
    public Result<Product> getByCode(@ApiParam("商品编号") @PathVariable String productCode) {
        Product product = productService.getByCode(productCode);
        return Result.success(product);
    }

    @ApiOperation("新增商品")
    @PostMapping
    public Result<Boolean> save(@RequestBody Product product) {
        log.info("=== 新增商品 ===");
        log.info("接收到的商品数据: {}", product);
        log.info("商品编号: {}", product.getProductCode());
        log.info("商品名称: {}", product.getProductName());
        log.info("商品分类: {}", product.getCategory());
        log.info("商品价格: {}", product.getPrice());
        try {
            boolean result = productService.save(product);
            log.info("商品保存成功，返回结果: {}", result);
            return Result.success(result);
        } catch (Exception e) {
            log.error("❌ 商品保存失败", e);
            throw e;
        }
    }

    @ApiOperation("更新商品")
    @PutMapping
    public Result<Boolean> update(@RequestBody Product product) {
        boolean result = productService.update(product);
        return Result.success(result);
    }

    @ApiOperation("删除商品")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@ApiParam("商品ID") @PathVariable Long id) {
        boolean result = productService.deleteById(id);
        return Result.success(result);
    }

    @ApiOperation("查询所有商品")
    @GetMapping("/list")
    public Result<List<Product>> listAll() {
        List<Product> list = productService.listAll();
        return Result.success(list);
    }

    @ApiOperation("根据分类查询商品列表")
    @GetMapping("/list/category/{category}")
    public Result<List<Product>> listByCategory(@ApiParam("商品分类") @PathVariable String category) {
        List<Product> list = productService.listByCategory(category);
        return Result.success(list);
    }

    @ApiOperation("查询上架商品列表")
    @GetMapping("/list/onsale")
    public Result<List<Product>> listOnSale() {
        List<Product> list = productService.listOnSale();
        return Result.success(list);
    }

    @ApiOperation("查询热门商品列表")
    @GetMapping("/list/hot")
    public Result<List<Product>> listHot(@ApiParam("数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        List<Product> list = productService.listHot(limit);
        return Result.success(list);
    }

    @ApiOperation("查询新品列表")
    @GetMapping("/list/new")
    public Result<List<Product>> listNew(@ApiParam("数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        List<Product> list = productService.listNew(limit);
        return Result.success(list);
    }

    @ApiOperation("分页查询商品列表")
    @GetMapping("/page")
    public Result<PageResult<Product>> page(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Product> pageResult = productService.page(pageNum, pageSize);
        return Result.success(pageResult);
    }

    @ApiOperation("根据条件分页查询")
    @GetMapping("/page/condition")
    public Result<PageResult<Product>> pageByCondition(
            @ApiParam("商品编号") @RequestParam(required = false) String productCode,
            @ApiParam("商品名称") @RequestParam(required = false) String productName,
            @ApiParam("商品分类") @RequestParam(required = false) String category,
            @ApiParam("状态") @RequestParam(required = false) Integer status,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Product> pageResult = productService.pageByCondition(
                productCode, productName, category, status, pageNum, pageSize);
        return Result.success(pageResult);
    }

    @ApiOperation("更新商品状态")
    @PutMapping("/{id}/status/{status}")
    public Result<Boolean> updateStatus(
            @ApiParam("商品ID") @PathVariable Long id,
            @ApiParam("状态") @PathVariable Integer status) {
        boolean result = productService.updateStatus(id, status);
        return Result.success(result);
    }

    @ApiOperation("更新库存数量")
    @PutMapping("/{id}/stock/{quantity}")
    public Result<Boolean> updateStock(
            @ApiParam("商品ID") @PathVariable Long id,
            @ApiParam("库存数量") @PathVariable Integer quantity) {
        boolean result = productService.updateStock(id, quantity);
        return Result.success(result);
    }

    @ApiOperation("查询库存不足的商品")
    @GetMapping("/list/lowStock")
    public Result<List<Product>> listLowStock() {
        List<Product> list = productService.listLowStock();
        return Result.success(list);
    }

}



