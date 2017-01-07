@echo off
echo 正在注册服务……
"%~dp0FIR_AIX64Service.exe" /service && goto 0
echo 注册失败，您可能未以管理员身份运行！
goto t

:0
net start FIR_AIX64Service && goto t
echo 启动服务失败，请联系软件发布者！
goto t

:t
pause
