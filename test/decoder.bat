@echo off
chcp 65001 2>nul >nul
set key=.\keys\181107.00\key.pr
set /p code=enter string: 
java "-Dfile.encoding=UTF8" -jar coder.jar -d %key% "%code%"
pause