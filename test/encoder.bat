@echo off
chcp 65001 2>nul >nul
set key=.\key.pub
java "-Dfile.encoding=UTF8" -jar coder.jar -e %key% "Очень секретное сообщение!"
pause