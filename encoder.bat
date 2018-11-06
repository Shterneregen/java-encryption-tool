@echo off
chcp 65001 2>nul >nul
java "-Dfile.encoding=UTF8" -jar coder.jar -e .\key.pub "Очень секретное сообщение!"
pause