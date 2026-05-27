import request from '@/utils/request'

/**
 * 上传图片文件
 */
export const uploadImage = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/api/v1/upload/image',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 保存图片URL
 */
export const saveImageUrl = (imageUrl) => {
  return request({
    url: '/api/v1/upload/save-url',
    method: 'post',
    params: { imageUrl }
  })
}

/**
 * 删除图片
 */
export const deleteImage = (imageUrl) => {
  return request({
    url: '/api/v1/upload/image',
    method: 'delete',
    params: { imageUrl }
  })
}





