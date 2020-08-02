@echo off
set /p FILE=Enter file name to decrypt: 
set /p PASSWORD=Enter password: 
java -jar coder.jar -dp "%FILE%" "%PASSWORD%"
pause