# 飞跃滑雪场管理系统 - 后端项目

## 📖 项目说明

基于Spring Boot 2.7.18开发的滑雪场管理系统后端服务。

## 🛠️ 技术栈

- **Spring Boot** 2.7.18 - 核心框架
- **MyBatis** 2.3.1 - 持久层框架
- **MySQL** 8.0 - 数据库
- **Redis** - 缓存
- **Spring Security** - 安全框架
- **JWT** - Token认证
- **Knife4j** 3.0.3 - API文档
- **Hutool** 5.8.22 - 工具包
- **Lombok** - 简化代码

## 📂 项目结构

```
backend/
├── pom.xml                          # Maven配置
├── sql/                             # SQL脚本
│   ├── schema.sql                   # 建表脚本（15张表）
│   └── init_data.sql                # 测试数据
└── src/main/
    ├── java/com/ski/resort/
    │   ├── SkiResortApplication.java    # 主启动类
    │   ├── common/                      # 公共类
    │   │   ├── Result.java              # 统一返回结果
    │   │   ├── ResultCode.java          # 返回状态码枚举
    │   │   ├── PageResult.java          # 分页结果
    │   │   └── Constants.java           # 系统常量
    │   ├── config/                      # 配置类
    │   │   ├── Knife4jConfig.java       # API文档配置
    │   │   └── CorsConfig.java          # 跨域配置
    │   ├── controller/                  # 控制器层
    │   │   └── UserController.java      # 用户控制器 ✅
    │   ├── entity/                      # 实体类
    │   │   └── User.java                # 用户实体 ✅
    │   ├── exception/                   # 异常处理
    │   │   ├── BusinessException.java
    │   │   └── GlobalExceptionHandler.java
    │   ├── mapper/                      # Mapper接口
    │   │   └── UserMapper.java          # 用户Mapper ✅
    │   └── service/                     # 业务层
    │       ├── UserService.java         # 用户Service接口 ✅
    │       └── impl/
    │           └── UserServiceImpl.java # 用户Service实现 ✅
    └── resources/
        ├── application.yml              # 配置文件
        └── mapper/
            └── UserMapper.xml           # MyBatis映射文件 ✅
```

## 🚀 快速开始

### 1. 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0
- Redis（可选）

### 2. 创建数据库

```bash
# 登录MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE ski_resort CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 使用数据库
USE ski_resort;

# 执行建表脚本
source /path/to/backend/sql/schema.sql;

# 执行测试数据脚本（可选）
source /path/to/backend/sql/init_data.sql;
```

或者直接在MySQL客户端（Navicat、DBeaver等）中执行`sql/schema.sql`和`sql/init_data.sql`。

### 3. 修改配置

修改`src/main/resources/application.yml`中的数据库配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ski_resort?...
    username: root
    password: 你的MySQL密码  # 修改这里
```

如果使用Redis，修改Redis配置：

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password:  # 如果有密码就填写
```

### 4. 启动项目

#### 方式一：使用IDEA

1. 用IDEA打开`backend`文件夹
2. 等待Maven下载依赖
3. 运行`SkiResortApplication.java`的main方法

#### 方式二：使用Maven命令

```bash
# 进入backend目录
cd backend

# 清理并安装依赖
mvn clean install

# 启动项目
mvn spring-boot:run
```

### 5. 验证启动

启动成功后，控制台会显示：

```
========================================
飞跃滑雪场管理系统启动成功！
API文档地址：http://localhost:8080/api/doc.html
========================================
```

访问以下地址测试：

- **测试接口**: http://localhost:8080/api/user/test
- **API文档**: http://localhost:8080/api/doc.html
- **用户列表**: http://localhost:8080/api/user/list

## 📚 API文档

启动项目后访问：http://localhost:8080/api/doc.html

Knife4j提供了可视化的API文档，可以在线测试接口。

## 🔧 已实现的功能

### 用户模块 ✅

- [x] 用户注册
- [x] 用户登录（简化版，未集成JWT）
- [x] 用户信息查询（按ID、用户名、手机号）
- [x] 用户列表查询（全部、分页、条件查询）
- [x] 按角色查询用户
- [x] 添加用户
- [x] 更新用户
- [x] 删除用户（逻辑删除）
- [x] 修改密码
- [x] 重置密码

## 📝 测试账号

执行`init_data.sql`后，可以使用以下测试账号：

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | 123456 | admin | 管理员 |
| user001 | 123456 | user | 普通用户 |
| coach001 | 123456 | coach | 教练 |
| staff001 | 123456 | staff | 工作人员 |

## 🎯 待开发模块

- [ ] 雪具管理模块
- [ ] 租赁订单模块
- [ ] 教练管理模块
- [ ] 教练预约模块
- [ ] 商品管理模块
- [ ] 销售订单模块
- [ ] 课程管理模块
- [ ] 场地管理模块
- [ ] 会员卡管理模块
- [ ] 数据统计模块
- [ ] 系统日志模块

## ⚠️ 注意事项

1. **数据库密码**：请修改`application.yml`中的MySQL密码
2. **Redis**：如果不需要Redis功能，可以先注释掉相关配置
3. **端口占用**：默认端口8080，如果被占用请修改配置
4. **密码加密**：密码使用BCrypt加密，初始测试密码都是`123456`
5. **日志文件**：日志保存在`logs/ski-resort.log`

## 🔍 常见问题

### 1. 启动失败：连接数据库失败

**原因**：MySQL未启动或配置错误

**解决**：
- 检查MySQL是否启动
- 检查`application.yml`中的数据库配置
- 检查数据库`ski_resort`是否已创建

### 2. Mapper找不到

**原因**：MyBatis扫描路径配置错误

**解决**：
- 确认`@MapperScan("com.ski.resort.mapper")`注解存在
- 确认Mapper.xml文件在`resources/mapper/`目录下

### 3. 依赖下载慢

**原因**：Maven仓库在国外

**解决**：
- 配置Maven阿里云镜像
- 参考《🚀快速开始指南.md》中的Maven配置

## 📖 开发规范

### 代码规范
- 类名：大驼峰 (UserController)
- 方法名：小驼峰 (getUserList)
- 常量：全大写下划线 (MAX_SIZE)
- 包名：全小写 (com.ski.resort)

### 注释规范
- 每个类都要有类注释（作用、作者、日期）
- 公开方法都要有方法注释
- 复杂逻辑要有行内注释

### Git提交规范
- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `refactor`: 重构代码

## 📞 联系方式

- 开发者：XXX
- 学校：河北东方学院
- 专业：计算机科学与技术

---

**最后更新时间**：2025年10月8日  
**当前版本**：v0.1.0-dev  
**开发状态**：🔄 用户模块已完成，其他模块开发中



