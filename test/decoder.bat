@echo off
chcp 65001 2>nul >nul
set key=.\key.key
set /p code=Enter encoded message: 
java "-Dfile.encoding=UTF8" -jar coder.jar -d %key% "%code%"
pause