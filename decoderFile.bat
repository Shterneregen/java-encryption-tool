@echo off
chcp 65001 2>nul >nul

REM set key=.\key
set key=.\181107.0\key
REM set key=.\181107

set file=.\text.txt

java "-Dfile.encoding=UTF8" -jar coder.jar -df %key% %file%
REM java "-Dfile.encoding=UTF8" -jar coder.jar -df %key% %file%
pause