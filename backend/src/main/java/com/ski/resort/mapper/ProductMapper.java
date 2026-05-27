package com.ski.resort.mapper;

import com.ski.resort.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品Mapper接口
 *
 * @author XXX
 * @date 2025-10-08
 */
@Mapper
public interface ProductMapper {

    /**
     * 根据ID查询商品
     */
    Product selectById(@Param("id") Long id);

    /**
     * 根据商品编号查询
     */
    Product selectByCode(@Param("productCode") String productCode);

    /**
     * 插入商品
     */
    int insert(Product product);

    /**
     * 更新商品
     */
    int update(Product product);

    /**
     * 根据ID删除商品（逻辑删除）
     */
    int deleteById(@Param("id") Long id);

    /**
     * 查询所有商品
     */
    List<Product> selectAll();

    /**
     * 根据分类查询商品列表
     */
    List<Product> selectByCategory(@Param("category") String category);

    /**
     * 查询上架商品列表
     */
    List<Product> selectOnSale();

    /**
     * 查询热门商品列表
     */
    List<Product> selectHot(@Param("limit") Integer limit);

    /**
     * 查询新品列表
     */
    List<Product> selectNew(@Param("limit") Integer limit);

    /**
     * 分页查询商品列表
     */
    List<Product> selectPage(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 统计商品总数
     */
    Long countAll();

    /**
     * 根据条件统计商品数
     */
    Long countByCondition(@Param("productCode") String productCode,
                          @Param("productName") String productName,
                          @Param("category") String category,
                          @Param("status") Integer status);

    /**
     * 根据条件查询商品列表
     */
    List<Product> selectByCondition(@Param("productCode") String productCode,
                                     @Param("productName") String productName,
                                     @Param("category") String category,
                                     @Param("status") Integer status,
                                     @Param("offset") Integer offset,
                                     @Param("limit") Integer limit);

    /**
     * 更新商品状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新库存数量
     */
    int updateStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 增加已售数量
     */
    int incrementSoldQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 查询库存不足的商品
     */
    List<Product> selectLowStock();

}



