
@echo off

"%~dp0FIR_AIX64Service.exe" /unregserver && goto 0
echo 注销失败！您可能未以管理员身份运行或该服务未被注册！
goto t

:0
echo 注销成功！


:t
pause