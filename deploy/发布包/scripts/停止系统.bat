@echo off
chcp 65001 >nul
title 飞跃滑雪场管理系统 - 停止服务

echo ============================================
echo     飞跃滑雪场管理系统 - 停止所有服务
echo ============================================
echo.

echo [1/3] 停止 Nginx...
taskkill /f /im nginx.exe >nul 2>&1
echo       Nginx 已停止 ✓

echo [2/3] 停止后端Java进程...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') do (
    taskkill /f /pid %%a >nul 2>&1
)
echo       后端服务已停止 ✓

echo [3/3] 停止 Redis...
taskkill /f /im redis-server.exe >nul 2>&1
echo       Redis 已停止 ✓

echo.
echo ============================================
echo     全部服务已停止！
echo ============================================
pause
