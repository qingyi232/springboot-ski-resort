@echo off
chcp 65001 >nul
color 0A
echo ========================================
echo    飞跃滑雪场管理系统 - 后端服务
echo ========================================
echo.
echo [1/3] 检查环境...
java -version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到 Java 环境！
    echo 请先安装 JDK 17
    pause
    exit
)
echo [✓] Java 环境正常
echo.

echo [2/3] 进入后端目录...
cd /d %~dp0backend
if not exist "resort-backend-0.0.1-SNAPSHOT.jar" (
    echo [错误] 找不到后端 jar 文件！
    echo 请确认文件位置：backend\resort-backend-0.0.1-SNAPSHOT.jar
    pause
    exit
)
echo [✓] 找到后端文件
echo.

echo [3/3] 启动后端服务...
echo.
echo ========================================
echo  后端服务启动中，请稍候...
echo  启动成功后会显示：Started SkiResortApplication
echo  ⚠️ 请勿关闭此窗口！
echo ========================================
echo.

java -jar resort-backend-0.0.1-SNAPSHOT.jar

pause
