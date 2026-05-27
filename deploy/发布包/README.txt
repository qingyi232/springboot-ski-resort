=============================================
  飞跃滑雪场管理系统 v1.0 - 部署说明
=============================================

【发布包目录结构】
  backend/          → 后端JAR包和配置文件
  frontend/         → 前端静态文件（Vue构建产物）
  sql/              → 数据库初始化脚本
  scripts/          → 一键启动/停止脚本
  nginx.conf        → Nginx配置文件
  部署指南.md       → 完整部署文档

=============================================
【快速部署步骤（共9步）】
=============================================

第1步：安装必要软件
  - JDK 17：https://www.oracle.com/java/technologies/downloads/#java17
  - MySQL 8.0：https://dev.mysql.com/downloads/installer/
  - Redis：https://github.com/tporadowski/redis/releases
  - Nginx：https://nginx.org/en/download.html
  （安装完成后配置好环境变量，确保java/mysql命令可用）

第2步：初始化数据库
  启动MySQL → 打开Navicat或命令行 → 执行 sql\initdatabase.sql

第3步：修改数据库密码（如果不是root）
  用记事本打开 backend\application.yml
  找到 password: root 改为你的MySQL密码

第4步：部署前端到Nginx
  将 frontend\ 文件夹内所有文件 复制到 Nginx安装目录\html\ 下
  （覆盖原有的 index.html）

第5步：配置Nginx
  用本包中的 nginx.conf 替换 Nginx安装目录\conf\nginx.conf

第6步：启动Redis
  找到Redis安装目录，双击 redis-server.exe

第7步：启动后端
  打开cmd，进入本目录，执行：
  java -jar backend\ski-resort-backend-1.0.0.jar

第8步：启动Nginx
  进入Nginx安装目录，双击 nginx.exe

第9步：打开浏览器
  访问 http://localhost

=============================================
【测试账号】
=============================================
  管理员：admin / 123456      → 管理所有数据
  教练：  coach1~5 / 123456   → 管理课程预约
  工作人员：staff1~2 / 123456 → 前台租赁办理
  用户：  testuser / 123456   → 浏览、租赁、预约

=============================================
【端口说明】
=============================================
  80   → Nginx（前端页面）
  8080 → 后端API服务
  3306 → MySQL数据库
  6379 → Redis缓存

=============================================
【常见问题】
=============================================
Q：后端启动失败提示数据库连接错误？
A：检查MySQL是否启动，密码是否修改正确。

Q：页面刷新出现404？
A：Nginx配置未正确替换，重新用nginx.conf覆盖。

Q：端口被占用？
A：cmd执行 netstat -ano | findstr :端口号 查看占用进程，
   用 taskkill /PID 进程号 /F 结束。

Q：如何停止所有服务？
A：运行 scripts\停止系统.bat
