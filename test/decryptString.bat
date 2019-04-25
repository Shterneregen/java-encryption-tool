@echo off
chcp 65001 2>nul >nul
set KEY=.\key.key
set /p STRING_TO_DECRYPT=Enter encoded message: 
java "-Dfile.encoding=UTF8" -jar coder.jar -d %KEY% "%STRING_TO_DECRYPT%"
pause