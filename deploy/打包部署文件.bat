@echo off
chcp 65001 >nul
title 飞跃滑雪场管理系统 - 打包部署文件

echo ============================================
echo     打包部署文件 - 准备发送给客户
echo ============================================
echo.

set PROJECT_DIR=%~dp0..
set OUTPUT_DIR=%PROJECT_DIR%\deploy\发布包

:: 创建输出目录
if exist "%OUTPUT_DIR%" rmdir /s /q "%OUTPUT_DIR%"
mkdir "%OUTPUT_DIR%"
mkdir "%OUTPUT_DIR%\backend"
mkdir "%OUTPUT_DIR%\frontend"
mkdir "%OUTPUT_DIR%\sql"
mkdir "%OUTPUT_DIR%\scripts"

echo [1/4] 打包后端JAR...
cd /d %PROJECT_DIR%\backend
call mvn clean package -DskipTests -q
if errorlevel 1 (
    echo       后端打包失败！请检查Maven环境。
    pause
    exit /b 1
)
copy target\ski-resort-backend-1.0.0.jar "%OUTPUT_DIR%\backend\" >nul
copy src\main\resources\application.yml "%OUTPUT_DIR%\backend\" >nul
copy src\main\resources\application-ai.yml "%OUTPUT_DIR%\backend\" >nul
echo       后端JAR打包完成 ✓

echo [2/4] 构建前端...
cd /d %PROJECT_DIR%\frontend
call npm install --silent
call npm run build
xcopy dist\* "%OUTPUT_DIR%\frontend\" /s /e /q >nul
echo       前端构建完成 ✓

echo [3/4] 复制SQL和配置文件...
copy "%PROJECT_DIR%\backend\sql\initdatabase.sql" "%OUTPUT_DIR%\sql\" >nul
copy "%PROJECT_DIR%\deploy\nginx.conf" "%OUTPUT_DIR%\" >nul
copy "%PROJECT_DIR%\deploy\启动系统.bat" "%OUTPUT_DIR%\scripts\" >nul
copy "%PROJECT_DIR%\deploy\停止系统.bat" "%OUTPUT_DIR%\scripts\" >nul
copy "%PROJECT_DIR%\部署指南.md" "%OUTPUT_DIR%\" >nul
echo       配置文件复制完成 ✓

echo [4/4] 生成客户操作说明...
(
echo =====================================
echo  飞跃滑雪场管理系统 - 部署说明
echo =====================================
echo.
echo 文件结构：
echo   backend\          后端JAR包和配置文件
echo   frontend\         前端静态文件
echo   sql\              数据库初始化脚本
echo   scripts\          启动/停止脚本
echo   nginx.conf        Nginx配置文件
echo   部署指南.md        详细部署文档
echo.
echo 快速部署步骤：
echo   1. 安装 JDK17 + MySQL8 + Redis + Nginx
echo   2. 启动MySQL，执行 sql\initdatabase.sql
echo   3. 如MySQL密码不是root，修改 backend\application.yml
echo   4. 将 frontend\ 内容复制到 Nginx安装目录\html\ 下
echo   5. 用 nginx.conf 替换 Nginx安装目录\conf\nginx.conf
echo   6. 启动Redis
echo   7. 运行：java -jar backend\ski-resort-backend-1.0.0.jar
echo   8. 启动Nginx
echo   9. 浏览器打开 http://localhost
echo.
echo 管理员账号：admin / 123456
echo =====================================
) > "%OUTPUT_DIR%\README.txt"
echo       客户说明生成完成 ✓

echo.
echo ============================================
echo     打包完成！发布包位置：
echo     %OUTPUT_DIR%
echo.
echo     将整个"发布包"文件夹发送给客户即可
echo ============================================
pause
