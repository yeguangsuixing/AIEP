@echo off
echo ����ע����񡭡�
"%~dp0FIR_AIX64Service.exe" /service && goto 0
echo ע��ʧ�ܣ�������δ�Թ���Ա������У�
goto t

:0
net start FIR_AIX64Service && goto t
echo ��������ʧ�ܣ�����ϵ��������ߣ�
goto t

:t
pause
