@echo off
set KEY=.\key.key
set /p FILE=Enter file name to decrypt: 
java -jar coder.jar -df %KEY% %FILE%
pause