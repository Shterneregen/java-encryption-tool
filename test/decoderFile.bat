@echo off
chcp 65001 2>nul >nul

set key=.\keys\181107.00\key.pr
REM set key=.\181107

set file=.\text.txt

java "-Dfile.encoding=UTF8" -jar coder.jar -df %key% %file%
REM java -jar coder.jar -df %key% %file%
pause