package com.ski.resort.service.impl;

import com.ski.resort.common.PageResult;
import com.ski.resort.common.ResultCode;
import com.ski.resort.entity.Product;
import com.ski.resort.exception.BusinessException;
import com.ski.resort.mapper.ProductMapper;
import com.ski.resort.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品Service实现类
 *
 * @author XXX
 * @date 2025-10-08
 */
@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public Product getById(Long id) {
        if (id == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.FAILED);
        }
        return product;
    }

    @Override
    public Product getByCode(String productCode) {
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }
        return productMapper.selectByCode(productCode);
    }

    @Override
    @Transactional
    public boolean save(Product product) {
        log.info("=== ProductService.save 开始 ===");
        log.info("商品数据: {}", product);
        
        // 验证商品编号
        if (product.getProductCode() == null || product.getProductCode().trim().isEmpty()) {
            log.error("❌ 商品编号为空");
            throw new BusinessException("商品编号不能为空");
        }
        log.info("✅ 商品编号验证通过: {}", product.getProductCode());
        
        // 验证商品名称
        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            log.error("❌ 商品名称为空");
            throw new BusinessException("商品名称不能为空");
        }
        log.info("✅ 商品名称验证通过: {}", product.getProductName());

        // 检查商品编号是否已存在
        log.info("检查商品编号是否重复: {}", product.getProductCode());
        Product existingProduct = productMapper.selectByCode(product.getProductCode());
        if (existingProduct != null) {
            log.error("❌ 商品编号已存在: {}", product.getProductCode());
            throw new BusinessException("商品编号[" + product.getProductCode() + "]已存在");
        }
        log.info("✅ 商品编号唯一性检查通过");

        // 设置默认值
        if (product.getStatus() == null) {
            product.setStatus(1);
            log.info("设置默认状态: 1（上架）");
        }
        if (product.getIsHot() == null) {
            product.setIsHot(0);
            log.info("设置默认热门标志: 0");
        }
        if (product.getIsNew() == null) {
            product.setIsNew(0);
            log.info("设置默认新品标志: 0");
        }
        if (product.getSoldQuantity() == null) {
            product.setSoldQuantity(0);
            log.info("设置默认已售数量: 0");
        }
        if (product.getMinStock() == null) {
            product.setMinStock(10);
            log.info("设置默认最小库存: 10");
        }

        log.info("准备插入商品，完整数据: {}", product);
        try {
            int result = productMapper.insert(product);
            log.info("✅ 商品插入成功，影响行数: {}", result);
            return result > 0;
        } catch (Exception e) {
            log.error("❌ 商品插入数据库失败", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean update(Product product) {
        if (product.getId() == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }

        Product existingProduct = productMapper.selectById(product.getId());
        if (existingProduct == null) {
            throw new BusinessException(ResultCode.FAILED);
        }

        if (product.getProductCode() != null 
            && !product.getProductCode().equals(existingProduct.getProductCode())) {
            Product codeCheck = productMapper.selectByCode(product.getProductCode());
            if (codeCheck != null) {
                throw new BusinessException("商品编号已被使用");
            }
        }

        int result = productMapper.update(product);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        if (id == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }

        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.FAILED);
        }

        int result = productMapper.deleteById(id);
        return result > 0;
    }

    @Override
    public List<Product> listAll() {
        return productMapper.selectAll();
    }

    @Override
    public List<Product> listByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }
        return productMapper.selectByCategory(category);
    }

    @Override
    public List<Product> listOnSale() {
        return productMapper.selectOnSale();
    }

    @Override
    public List<Product> listHot(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        return productMapper.selectHot(limit);
    }

    @Override
    public List<Product> listNew(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        return productMapper.selectNew(limit);
    }

    @Override
    public PageResult<Product> page(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        int offset = (pageNum - 1) * pageSize;
        List<Product> list = productMapper.selectPage(offset, pageSize);
        Long total = productMapper.countAll();

        PageResult<Product> pageResult = new PageResult<>();
        pageResult.setTotal(total);
        pageResult.setPageNum(pageNum);
        pageResult.setPageSize(pageSize);
        pageResult.setTotalPages((int)((total + pageSize - 1) / pageSize));
        pageResult.setList(list);

        return pageResult;
    }

    @Override
    public PageResult<Product> pageByCondition(String productCode, String productName,
                                                String category, Integer status,
                                                Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        int offset = (pageNum - 1) * pageSize;
        List<Product> list = productMapper.selectByCondition(productCode, productName,
                category, status, offset, pageSize);
        Long total = productMapper.countByCondition(productCode, productName, category, status);

        PageResult<Product> pageResult = new PageResult<>();
        pageResult.setTotal(total);
        pageResult.setPageNum(pageNum);
        pageResult.setPageSize(pageSize);
        pageResult.setTotalPages((int)((total + pageSize - 1) / pageSize));
        pageResult.setList(list);

        return pageResult;
    }

    @Override
    @Transactional
    public boolean updateStatus(Long id, Integer status) {
        if (id == null || status == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }

        int result = productMapper.updateStatus(id, status);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean updateStock(Long id, Integer quantity) {
        if (id == null || quantity == null || quantity < 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }

        int result = productMapper.updateStock(id, quantity);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean incrementSoldQuantity(Long id, Integer quantity) {
        if (id == null || quantity == null || quantity <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED);
        }

        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.FAILED);
        }

        if (product.getStockQuantity() < quantity) {
            throw new BusinessException(ResultCode.FAILED);
        }

        int result = productMapper.incrementSoldQuantity(id, quantity);
        return result > 0;
    }

    @Override
    public List<Product> listLowStock() {
        return productMapper.selectLowStock();
    }

}



