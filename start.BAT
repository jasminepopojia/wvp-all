@echo off
setlocal enabledelayedexpansion

:: 检查端口 18080 是否被占用
echo Checking if port 18080 is in use...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :18080') do (
    set "pid=%%a"
    if not "!pid!" == "" (
        echo Port 18080 is used by PID !pid!, attempting to kill the process...
        taskkill /F /PID !pid!
    )
)

:: 定位到无线适配器 WLAN 的 IPv4 地址
set "adapter_found="
for /f "delims=" %%a in ('ipconfig') do (
    echo %%a | findstr /I "WLAN" > nul && set adapter_found=1
    if defined adapter_found (
        echo %%a | findstr /I "IPv4" > nul && (
            for /f "tokens=2 delims=:" %%i in ("%%a") do (
                set ip=%%i
                set ip=!ip:~1!
                goto update
            )
        )
    )
)

:update
:: 显示即将使用的IP地址
echo Using IP: %ip%

:: 更新application.yml文件
echo Updating application.yml...
powershell -Command "$content = (Get-Content 'wvp-gb28181-pro\target\application.yml' -Raw); $content = $content -replace '(sip:\r?\n\s+ip:)\s+\d+\.\d+\.\d+\.\d+', ('$1 ' + '%ip%'); $content = $content -replace '(media:\r?\n\s+ip:)\s+\d+\.\d+\.\d+\.\d+', ('$1 ' + '%ip%'); Set-Content 'wvp-gb28181-pro\target\application.yml' $content -Encoding UTF8"

:: 启动后端服务
::start cmd /k "cd wvp-gb28181-pro & java -jar target/wvp-pro-2.7.1-10091344.jar --spring.config.location=./target/application.yml"
start cmd /k "cd wvp-gb28181-pro & java -jar target/wvp-pro-2.7.1-10140950.jar --spring.config.location=./target/application.yml"
:: 启动前端服务
start cmd /k "cd wvp-gb28181-pro\web_src & npm run dev"

endlocal
