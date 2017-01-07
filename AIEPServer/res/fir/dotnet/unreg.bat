@echo off
c:
cd C:\Windows\Microsoft.NET\Framework\v4.0.30319
regasm /unregister "%~dp0FIR_AICSharpUtilLoader.dll"


pause