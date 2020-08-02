@echo off
chcp 65001 2>nul >nul
set KEY=.\key.key
set FILE=.\text.txt
java "-Dfile.encoding=UTF8" -jar coder.jar -sf %KEY% %FILE%
pause