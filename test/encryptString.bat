@echo off
chcp 65001 2>nul >nul
set KEY=.\key.pub
set /p STRING_TO_ENCRYPT=Enter message to encrypt:
java "-Dfile.encoding=UTF8" -jar coder.jar -e %KEY% %STRING_TO_ENCRYPT%
pause