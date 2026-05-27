@echo off
chcp 65001 >nul
color 0B
echo ========================================
echo    飞跃滑雪场管理系统 - 前端服务
echo ========================================
echo.
echo [1/3] 检查环境...
node -v >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到 Node.js 环境！
    echo 请先安装 Node.js
    pause
    exit
)
echo [✓] Node.js 环境正常
echo.

echo [2/3] 进入前端目录...
cd /d %~dp0
if not exist "package.json" (
    echo [错误] 找不到 package.json 文件！
    echo 请确认当前目录是否为 frontend 目录
    pause
    exit
)
echo [✓] 找到前端项目
echo.

echo [3/3] 启动前端服务...
echo.
echo ========================================
echo  前端服务启动中，请稍候...
echo  启动成功后会显示：Local: http://localhost:5173/
echo  ⚠️ 请勿关闭此窗口！
echo ========================================
echo.

npm run dev

pause
