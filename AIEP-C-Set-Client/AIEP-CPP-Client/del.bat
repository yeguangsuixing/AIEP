echo ��������VS2010�����в���Ҫ���ļ�
echo ��ȷ�����ļ������ڹ���Ŀ¼֮�в��ر�VS2010
echo ��ʼ�������Ե�......

echo ����sdf�ļ�
del /q/a/f/s *.sdf

echo ����ipch�ļ�
del /q/a/f/s ipch\*.*

echo ����Debug�ļ�
del /q/a/f/s Debug\*.obj
del /q/a/f/s Debug\*.tlog
del /q/a/f/s Debug\*.log
del /q/a/f/s Debug\*.idb
del /q/a/f/s Debug\*.pdb
del /q/a/f/s Debug\*.ilk
del /q/a/f/s Debug\*.pch
del /q/a/f/s Debug\*.bsc
del /q/a/f/s Debug\*.sbr

del /q/a/f/s JsonLib\Debug\*.*
del /q/a/f/s JsonLibTest\Debug\*.*
del /q/a/f/s DllClass\Debug\*.*
del /q/a/f/s AIEPClient\Debug\*.*
del /q/a/f/s DllClassTest\Debug\*.*
del /q/a/f/s DataTransmit\Debug\*.*

echo ����Release�ļ�
del /q/a/f/s Release\*.obj
del /q/a/f/s Release\*.tlog
del /q/a/f/s Release\*.log
del /q/a/f/s Release\*.idb
del /q/a/f/s Release\*.pdb
del /q/a/f/s Release\*.ilk
del /q/a/f/s Release\*.pch


del /q/a/f/s JsonLib\Release\*.*
del /q/a/f/s JsonLibTest\Release\*.*
del /q/a/f/s DllClass\Release\*.*
del /q/a/f/s AIEPClient\Release\*.*

echo ����Temp�ļ�
del /q/a/f/s Temp\*.*

exit
ECHO �ļ�������ϣ���������3����˳������ڽ��뵹��ʱ......... 
@echo off 
echo WScript.Sleep 300 > %temp%.\tmp$$$.vbs 
set /a i =3 
:Timeout 
if %i% == 0 goto Next 
setlocal 
set /a i = %i% - 1 
echo ����ʱ����%i% 
cscript //nologo %temp%.\tmp$$$.vbs 
goto Timeout 
goto End 
:Next 
cls & 
echo.