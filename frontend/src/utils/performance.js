/**
 * 性能优化工具类
 */

/**
 * 防抖函数
 * @param {Function} fn 需要防抖的函数
 * @param {Number} delay 延迟时间（毫秒）
 * @returns {Function} 防抖后的函数
 */
export function debounce(fn, delay = 300) {
  let timer = null
  return function (...args) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

/**
 * 节流函数
 * @param {Function} fn 需要节流的函数
 * @param {Number} delay 延迟时间（毫秒）
 * @returns {Function} 节流后的函数
 */
export function throttle(fn, delay = 300) {
  let timer = null
  let lastTime = 0
  return function (...args) {
    const now = Date.now()
    if (now - lastTime >= delay) {
      fn.apply(this, args)
      lastTime = now
    }
  }
}

/**
 * 图片懒加载
 */
export const lazyLoad = {
  mounted(el, binding) {
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          el.src = binding.value
          observer.unobserve(el)
        }
      },
      {
        threshold: 0.1
      }
    )
    observer.observe(el)
  }
}

/**
 * 优化长列表渲染
 * 虚拟滚动配置
 */
export const virtualScrollConfig = {
  itemSize: 60, // 每项高度
  buffer: 5 // 缓冲区数量
}

/**
 * 格式化文件大小
 * @param {Number} bytes 字节数
 * @returns {String} 格式化后的大小
 */
export function formatFileSize(bytes) {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

/**
 * 深拷贝
 * @param {Object} obj 需要拷贝的对象
 * @returns {Object} 拷贝后的对象
 */
export function deepClone(obj) {
  if (obj === null || typeof obj !== 'object') return obj
  if (obj instanceof Date) return new Date(obj)
  if (obj instanceof Array) return obj.map(item => deepClone(item))
  
  const cloneObj = {}
  for (let key in obj) {
    if (obj.hasOwnProperty(key)) {
      cloneObj[key] = deepClone(obj[key])
    }
  }
  return cloneObj
}

/**
 * 本地存储封装（带过期时间）
 */
export const storage = {
  /**
   * 设置存储
   * @param {String} key 键
   * @param {Any} value 值
   * @param {Number} expire 过期时间（秒）
   */
  set(key, value, expire = null) {
    const data = {
      value,
      expire: expire ? Date.now() + expire * 1000 : null
    }
    localStorage.setItem(key, JSON.stringify(data))
  },
  
  /**
   * 获取存储
   * @param {String} key 键
   * @returns {Any} 值
   */
  get(key) {
    const str = localStorage.getItem(key)
    if (!str) return null
    
    try {
      const data = JSON.parse(str)
      if (data.expire && Date.now() > data.expire) {
        this.remove(key)
        return null
      }
      return data.value
    } catch {
      return null
    }
  },
  
  /**
   * 移除存储
   * @param {String} key 键
   */
  remove(key) {
    localStorage.removeItem(key)
  },
  
  /**
   * 清空存储
   */
  clear() {
    localStorage.clear()
  }
}

/**
 * 检测设备类型
 */
export function getDeviceType() {
  const ua = navigator.userAgent
  const isMobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(ua)
  const isTablet = /iPad|Android/i.test(ua) && !/Mobile/i.test(ua)
  
  if (isTablet) return 'tablet'
  if (isMobile) return 'mobile'
  return 'desktop'
}

/**
 * 请求动画帧节流
 */
export function rafThrottle(fn) {
  let isRunning = false
  return function (...args) {
    if (isRunning) return
    isRunning = true
    requestAnimationFrame(() => {
      fn.apply(this, args)
      isRunning = false
    })
  }
}

/**
 * 数组分块处理（避免长任务阻塞）
 * @param {Array} array 数组
 * @param {Function} process 处理函数
 * @param {Number} chunkSize 每批处理数量
 */
export async function processArrayInChunks(array, process, chunkSize = 100) {
  for (let i = 0; i < array.length; i += chunkSize) {
    const chunk = array.slice(i, i + chunkSize)
    await new Promise(resolve => {
      setTimeout(() => {
        chunk.forEach(process)
        resolve()
      }, 0)
    })
  }
}

/**
 * 预加载图片
 * @param {Array} urls 图片URL数组
 * @returns {Promise} 加载完成Promise
 */
export function preloadImages(urls) {
  const promises = urls.map(url => {
    return new Promise((resolve, reject) => {
      const img = new Image()
      img.onload = () => resolve(url)
      img.onerror = () => reject(url)
      img.src = url
    })
  })
  return Promise.allSettled(promises)
}

/**
 * 生成唯一ID
 * @returns {String} 唯一ID
 */
export function generateUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    const r = Math.random() * 16 | 0
    const v = c === 'x' ? r : (r & 0x3 | 0x8)
    return v.toString(16)
  })
}

/**
 * 时间格式化
 * @param {Date|String|Number} date 日期
 * @param {String} format 格式
 * @returns {String} 格式化后的时间
 */
export function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')
  
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 金额格式化
 * @param {Number} amount 金额
 * @param {Number} decimals 小数位数
 * @returns {String} 格式化后的金额
 */
export function formatMoney(amount, decimals = 2) {
  if (isNaN(amount)) return '0.00'
  return parseFloat(amount).toFixed(decimals).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

/**
 * 时间格式化（别名）
 * @param {Date|String|Number} date 日期
 * @param {String} format 格式
 * @returns {String} 格式化后的时间
 */
export function formatDateTime(date, format = 'YYYY-MM-DD HH:mm:ss') {
  return formatDate(date, format)
}


