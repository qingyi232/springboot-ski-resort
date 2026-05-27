# 飞跃滑雪场管理系统 - 前端项目

## 技术栈

- **框架**: Vue 3 + Vite
- **UI组件**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router
- **HTTP客户端**: Axios
- **图表**: ECharts
- **CSS预处理器**: Sass

## 项目结构

```
frontend/
├── public/                # 静态资源
├── src/
│   ├── api/              # API接口
│   │   ├── user.js
│   │   ├── equipment.js
│   │   ├── coach.js
│   │   ├── product.js
│   │   ├── course.js
│   │   ├── venue.js
│   │   └── rental-order.js
│   ├── assets/           # 资源文件
│   ├── components/       # 公共组件
│   ├── layout/           # 布局组件
│   │   └── MainLayout.vue
│   ├── router/           # 路由配置
│   │   └── index.js
│   ├── stores/           # 状态管理
│   │   └── user.js
│   ├── utils/            # 工具函数
│   │   └── request.js
│   ├── views/            # 页面组件
│   │   ├── Login.vue
│   │   ├── Dashboard.vue
│   │   ├── equipment/
│   │   ├── coach/
│   │   ├── product/
│   │   ├── course/
│   │   ├── venue/
│   │   ├── order/
│   │   └── user/
│   ├── App.vue
│   └── main.js
├── index.html
├── package.json
├── vite.config.js
└── README.md
```

## 快速开始

### 安装依赖

```bash
cd frontend
npm install
```

### 开发环境运行

```bash
npm run dev
```

访问：`http://localhost:3000`

### 生产环境打包

```bash
npm run build
```

### 预览打包结果

```bash
npm run preview
```

## 主要功能

### 1. 用户认证
- 登录/注册
- JWT Token认证
- 路由守卫
- 快速登录（测试用）

### 2. 首页Dashboard
- 数据统计卡片
- 租赁趋势图表
- 课程报名分析
- 热门雪具/课程/教练排行

### 3. 雪具管理
- 雪具列表（分页、搜索）
- 新增/编辑/删除雪具
- 库存管理
- 租赁/归还操作

### 4. 教练管理
- 教练列表
- 评分管理
- 学员统计

### 5. 商品管理
- 商品列表
- 库存管理
- 热门/新品标记

### 6. 课程管理
- 课程列表
- 报名/取消
- 学员管理

### 7. 场地管理
- 场地列表
- 容量管理
- 难度分级

### 8. 租赁订单
- 订单列表
- 支付/归还
- 状态管理

### 9. 用户管理（管理员）
- 用户列表
- 角色管理
- 权限控制

## API代理配置

开发环境下，所有 `/api` 请求会被代理到 `http://localhost:8080`

```javascript
// vite.config.js
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## 默认测试账号

- **管理员**: admin / 123456
- **普通用户**: user01 / 123456

## 开发进度

- [x] 项目初始化
- [x] 路由配置
- [x] 状态管理
- [x] API封装
- [x] 登录/注册页面
- [x] 主布局
- [x] 首页Dashboard
- [x] 雪具管理页面
- [ ] 教练管理页面
- [ ] 商品管理页面
- [ ] 课程管理页面
- [ ] 场地管理页面
- [ ] 租赁订单页面
- [ ] 用户管理页面

## 注意事项

1. 确保后端服务已启动（http://localhost:8080）
2. 首次运行需要安装依赖
3. Element Plus 已配置中文语言包
4. 所有图标来自 @element-plus/icons-vue

## 构建部署

```bash
# 构建
npm run build

# 构建产物位于 dist/ 目录
# 可直接部署到静态服务器（如 Nginx）
```

---

**开发时间**: 2025年10月8日  
**框架版本**: Vue 3.3.4



