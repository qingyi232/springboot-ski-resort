@echo off
chcp 65001 >nul
title 飞跃滑雪场管理系统 - 一键启动

echo ============================================
echo     飞跃滑雪场管理系统 - 一键启动脚本
echo ============================================
echo.

:: ========== 配置区域（根据实际安装路径修改）==========
set REDIS_DIR=C:\Redis
set NGINX_DIR=C:\nginx
set BACKEND_JAR=backend\target\ski-resort-backend-1.0.0.jar
:: =====================================================

echo [1/3] 启动 Redis...
cd /d %REDIS_DIR%
start "" redis-server.exe
timeout /t 2 /nobreak >nul
echo       Redis 已启动 ✓
echo.

echo [2/3] 启动后端服务（端口8080）...
cd /d %~dp0..
start "SkiResort-Backend" java -jar %BACKEND_JAR%
timeout /t 5 /nobreak >nul
echo       后端服务已启动 ✓
echo.

echo [3/3] 启动 Nginx（端口80）...
cd /d %NGINX_DIR%
start "" nginx.exe
timeout /t 2 /nobreak >nul
echo       Nginx 已启动 ✓
echo.

echo ============================================
echo     全部服务已启动！
echo.
echo     访问地址：http://localhost
echo     API文档：http://localhost:8080/doc.html
echo.
echo     管理员账号：admin / 123456
echo     工作人员：staff1 / 123456
echo     教练：coach1 / 123456
echo     普通用户：testuser / 123456
echo ============================================
echo.
echo 按任意键打开浏览器...
pause >nul
start http://localhost
