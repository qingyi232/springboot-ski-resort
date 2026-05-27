# 🎿 飞跃滑雪场管理系统

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen.svg)
![Vue.js](https://img.shields.io/badge/Vue.js-3.3+-blue.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-orange.svg)
![Redis](https://img.shields.io/badge/Redis-5.0+-red.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

**基于Spring Boot的现代化滑雪场综合管理系统**

[项目介绍](#项目介绍) • [功能特性](#功能特性) • [技术栈](#技术栈) • [快速开始](#快速开始) • [项目文档](#项目文档)

</div>

---

## 📖 项目介绍

本项目是一个基于**Spring Boot + Vue.js**开发的现代化滑雪场管理系统，旨在通过信息化手段提升滑雪场的运营管理效率。系统采用前后端分离架构，集成JWT认证、Redis缓存等主流技术，涵盖雪具租赁、教练预约、装备销售、课程管理、场地管理等核心业务模块。

### 适用场景
- ✅ 计算机专业本科毕业设计
- ✅ 软件工程课程设计项目
- ✅ Spring Boot + Vue.js技术学习
- ✅ 小型滑雪场实际应用

### 项目背景
响应"三亿人参与冰雪运动"号召，针对滑雪场管理信息化程度低、业务流程不规范等问题，开发本系统实现业务流程数字化、资源调配智能化、数据统计可视化。

---

## ⭐ 功能特性

### 核心业务模块

| 模块 | 功能描述 | 状态 |
|-----|---------|-----|
| 🏂 **雪具租赁管理** | 雪具信息管理、租赁订单处理、库存管理、归还结算 | ✅ 已完成 |
| 👨‍🏫 **教练预约管理** | 教练信息管理、预约排班、预约确认、评价系统 | ✅ 已完成 |
| 🛍️ **装备销售管理** | 商品管理、销售订单处理、库存管理、物流跟踪 | ✅ 已完成 |
| 📚 **课程管理** | 课程发布、学员报名、课程安排、学员管理 | ✅ 已完成 |
| 🏔️ **场地管理** | 场地信息管理、场地预订、使用记录统计 | ✅ 已完成 |
| 👥 **用户管理** | 用户注册登录、权限控制、会员管理、个人中心 | ✅ 已完成 |
| 📊 **数据统计分析** | Dashboard数据看板、收入统计、订单统计、热门排行 | ✅ 已完成 |

### 系统特色功能

- 🔐 **JWT Token认证**：安全的身份认证和权限控制
- ⚡ **Redis缓存优化**：提升系统响应速度和性能
- 📱 **响应式设计**：适配PC、平板、手机等多种设备
- 📝 **API文档自动生成**：Knife4j提供在线接口文档和测试
- 🎨 **数据可视化**：ECharts图表展示运营数据
- 🔄 **前后端分离**：清晰的架构设计，易于维护和扩展

---

## 🛠️ 技术栈

### 后端技术

| 技术 | 版本 | 说明 |
|-----|------|-----|
| Spring Boot | 2.7.18 | 核心框架 |
| MyBatis | 2.3.1 | 持久层框架 |
| MySQL | 8.0+ | 数据库 |
| Redis | 5.0+ | 缓存 |
| JWT | 0.9.1 | Token认证 |
| Knife4j | 3.0.3 | API文档 |
| Lombok | - | 简化代码 |

### 前端技术

| 技术 | 版本 | 说明 |
|-----|------|-----|
| Vue.js | 3.3+ | 前端框架 |
| Vite | 4.4+ | 构建工具 |
| Element Plus | - | UI组件库 |
| ECharts | 5.4+ | 数据可视化 |
| Axios | - | HTTP客户端 |
| Vue Router | - | 路由管理 |
| Pinia | - | 状态管理 |

---

## 🚀 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+
- Node.js 16.0+
- MySQL 8.0+
- Redis 5.0+

### 后端启动

```bash
# 1. 克隆项目（如果是从Git仓库）
git clone https://github.com/your-repo/ski-resort-system.git
cd ski-resort-system

# 2. 创建数据库并执行SQL脚本
mysql -u root -p
CREATE DATABASE ski_resort CHARACTER SET utf8mb4;
USE ski_resort;
SOURCE backend/sql/initdatabase.sql;

# 3. 修改配置文件
# 编辑 backend/src/main/resources/application.yml
# 修改数据库用户名密码和Redis配置

# 4. 启动后端
cd backend
mvn spring-boot:run

# 或者在IDE中直接运行 SkiResortApplication.java
```

### 前端启动

```bash
# 1. 安装依赖
cd frontend
npm install

# 2. 启动开发服务器
npm run dev

# 3. 访问系统
# 浏览器打开：http://localhost:5173
```

### 访问地址

- **前端地址**：http://localhost:5173
- **后端API**：http://localhost:8080
- **API文档**：http://localhost:8080/doc.html

### 测试账号

详细测试账号请查看：[测试账号说明.txt](测试账号说明.txt)

| 角色 | 用户名 | 密码 | 说明 |
|-----|--------|-----|-----|
| 管理员 | admin | 123456 | 最高权限，管理所有数据 |
| 教练 | coach1-5 | 123456 | 教练管理功能 |
| 普通用户 | testuser | 123456 | 用户端功能（浏览、租赁、预约） |

---

## 📁 项目结构

```
ski-resort-system/
├── backend/                    # 后端项目（Spring Boot）
│   ├── src/main/java/com/ski/resort/
│   │   ├── common/            # 通用类（Result、PageResult等）
│   │   ├── config/            # 配置类（CORS、JWT、Redis等）
│   │   ├── controller/        # 控制器（9个核心Controller）
│   │   ├── dto/              # 数据传输对象
│   │   ├── entity/           # 实体类（9个核心实体）
│   │   ├── exception/        # 异常处理
│   │   ├── mapper/           # MyBatis接口（9个Mapper）
│   │   ├── service/          # 服务接口和实现
│   │   └── util/            # 工具类（JWT、Redis等）
│   ├── src/main/resources/
│   │   ├── mapper/          # MyBatis XML映射文件
│   │   ├── application.yml  # 主配置文件
│   │   └── application-*.yml # 其他配置文件
│   ├── sql/
│   │   └── initdatabase.sql # 数据库初始化脚本
│   ├── pom.xml              # Maven配置
│   └── README.md            # 后端说明文档
│
├── frontend/                  # 前端项目（Vue.js）
│   ├── src/
│   │   ├── api/             # API接口封装
│   │   ├── components/      # 公共组件
│   │   ├── layout/         # 布局组件
│   │   ├── router/         # 路由配置
│   │   ├── stores/         # 状态管理（Pinia）
│   │   ├── utils/          # 工具类（Axios封装等）
│   │   ├── views/          # 页面组件
│   │   ├── App.vue         # 根组件
│   │   └── main.js         # 入口文件
│   ├── index.html
│   ├── package.json
│   ├── vite.config.js
│   └── README.md            # 前端说明文档
│
├── 报告文档/                  # 毕设相关文档
│   ├── 基于Spring+Boot的飞跃滑雪场管理系统的设计与实现.pptx
│   ├── 飞跃滑雪场管理系统开题答辩.pptx
│   ├── 开题报告_已填写.txt
│   ├── 任务书_已填写.txt
│   ├── 系统实践指导书_已填写.txt
│   └── ...
│
├── 功能要求/                  # 系统需求文档
│   ├── ski_resort_system.html
│   └── ski_system_architecture.html
│
├── 测试账号说明.txt           # 测试账号信息
└── README.md                # 项目说明（本文件）
```

---

## 📊 系统截图

### 登录页面
*待添加截图*

### Dashboard数据看板
*待添加截图*

### 雪具管理
*待添加截图*

### 教练预约
*待添加截图*

---

## 📚 项目文档

### 核心文档
- [📖 后端README](backend/README.md) - 后端项目说明
- [📱 前端README](frontend/README.md) - 前端项目说明
- [📋 测试账号说明.txt](测试账号说明.txt) - 系统测试账号信息

### 毕设文档
- [📁 报告文档](报告文档/) - 包含答辩PPT、开题报告、任务书等
- [📁 功能要求](功能要求/) - 系统需求和架构文档

### 数据库文档
- [📋 initdatabase.sql](backend/sql/initdatabase.sql) - 数据库初始化脚本（建表+数据）

### API文档
- 在线API文档：http://localhost:8080/doc.html （后端启动后访问）
- Swagger UI：自动生成的接口文档，支持在线测试

---

## 🎯 开发进度

- [x] 📊 数据库设计（15张表）
- [x] 🔧 后端框架搭建
- [x] 👤 用户管理模块
- [x] 🏂 雪具租赁模块
- [x] 👨‍🏫 教练管理模块
- [x] 📅 教练预约模块
- [x] 🛍️ 商品管理模块
- [x] 📦 销售订单模块
- [x] 📚 课程管理模块
- [x] 🏔️ 场地管理模块
- [x] 🔐 JWT认证集成
- [x] ⚡ Redis缓存集成
- [x] 📈 数据统计模块
- [x] 📝 API文档生成
- [ ] 🎨 前端完整页面开发（进行中 40%）
- [ ] 🔄 前后端联调
- [ ] ✅ 系统测试
- [ ] 🚀 部署上线

**总体完成度**：约75% ✅

---

## 💡 项目亮点

1. **完整的业务功能**：涵盖滑雪场7大核心业务模块，功能完善
2. **规范的代码结构**：严格遵循MVC三层架构，代码清晰易维护
3. **现代化技术栈**：Spring Boot 2.7 + Vue 3 + MySQL 8 + Redis
4. **JWT认证机制**：实现安全的用户认证和权限控制
5. **Redis缓存优化**：提升系统性能和响应速度
6. **完整的数据库设计**：15张表满足三范式要求
7. **RESTful API规范**：统一的接口设计和响应格式
8. **前后端分离**：清晰的职责划分，便于团队协作
9. **API文档自动生成**：Knife4j提供在线接口文档和测试
10. **统一异常处理**：友好的错误提示和日志记录

---

## 🤝 贡献指南

欢迎提交Issue和Pull Request！

### 开发规范
- **命名规范**：遵循Java和Vue.js命名规范
- **代码注释**：关键代码必须添加注释
- **提交规范**：使用语义化的提交信息
- **测试要求**：新功能需添加相应测试

---

## 📄 开源协议

本项目采用 [MIT](LICENSE) 开源协议。

---

## 📧 联系方式

- **作者**：XXX
- **学号**：202110101001
- **学院**：计算机学院
- **专业**：计算机科学与技术
- **邮箱**：xxx@example.com

---

## 🙏 致谢

感谢以下开源项目：
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Vue.js](https://vuejs.org/)
- [Element Plus](https://element-plus.org/)
- [MyBatis](https://mybatis.org/)
- [Knife4j](https://doc.xiaominfo.com/)
- [ECharts](https://echarts.apache.org/)

---

## ⚠️ 免责声明

本项目仅用于学习和研究目的，不得用于商业用途。使用本项目所产生的任何问题，作者不承担任何责任。

---

<div align="center">

**如果这个项目对你有帮助，请给个Star⭐️支持一下！**

Made with ❤️ by [Your Name]

</div>
