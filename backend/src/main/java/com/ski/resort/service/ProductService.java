package com.ski.resort.service;

import com.ski.resort.common.PageResult;
import com.ski.resort.entity.Product;

import java.util.List;

/**
 * 商品Service接口
 *
 * @author XXX
 * @date 2025-10-08
 */
public interface ProductService {

    /**
     * 根据ID查询商品
     */
    Product getById(Long id);

    /**
     * 根据商品编号查询
     */
    Product getByCode(String productCode);

    /**
     * 新增商品
     */
    boolean save(Product product);

    /**
     * 更新商品
     */
    boolean update(Product product);

    /**
     * 删除商品
     */
    boolean deleteById(Long id);

    /**
     * 查询所有商品
     */
    List<Product> listAll();

    /**
     * 根据分类查询商品列表
     */
    List<Product> listByCategory(String category);

    /**
     * 查询上架商品列表
     */
    List<Product> listOnSale();

    /**
     * 查询热门商品列表
     */
    List<Product> listHot(Integer limit);

    /**
     * 查询新品列表
     */
    List<Product> listNew(Integer limit);

    /**
     * 分页查询商品列表
     */
    PageResult<Product> page(Integer pageNum, Integer pageSize);

    /**
     * 根据条件分页查询
     */
    PageResult<Product> pageByCondition(String productCode, String productName,
                                        String category, Integer status,
                                        Integer pageNum, Integer pageSize);

    /**
     * 更新商品状态
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 更新库存数量
     */
    boolean updateStock(Long id, Integer quantity);

    /**
     * 增加已售数量（同时减少库存）
     */
    boolean incrementSoldQuantity(Long id, Integer quantity);

    /**
     * 查询库存不足的商品
     */
    List<Product> listLowStock();

}



