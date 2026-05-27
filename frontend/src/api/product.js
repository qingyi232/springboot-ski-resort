import request from '@/utils/request'

/**
 * 商品管理API
 */

// 获取商品列表
export function getProducts(params) {
  return request({
    url: '/api/v1/products/page',
    method: 'get',
    params
  })
}

// 获取商品列表（别名）
export function getProductList(params) {
  return getProducts(params)
}

// 获取商品详情
export function getProductById(id) {
  return request({
    url: `/api/v1/products/${id}`,
    method: 'get'
  })
}

// 创建商品（管理员）
export function createProduct(data) {
  return request({
    url: '/api/v1/products',
    method: 'post',
    data
  })
}

// 更新商品（管理员）
export function updateProduct(id, data) {
  return request({
    url: `/api/v1/products/${id}`,
    method: 'put',
    data
  })
}

// 删除商品（管理员）
export function deleteProduct(id) {
  return request({
    url: `/api/v1/products/${id}`,
    method: 'delete'
  })
}

// 更新商品库存
export function updateProductStock(id, stock) {
  return request({
    url: `/api/v1/products/${id}/stock`,
    method: 'put',
    data: { stock }
  })
}

// 更新商品状态
export function updateProductStatus(id, status) {
  return request({
    url: `/api/v1/products/${id}/status`,
    method: 'put',
    data: { status }
  })
}
