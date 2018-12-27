@echo off
chcp 65001 2>nul >nul
set key=.\keys\181107.00\181107.0
java "-Dfile.encoding=UTF8" -jar coder.jar -e %key% "Очень секретное сообщение!"
pause