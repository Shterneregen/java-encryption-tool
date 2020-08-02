@echo off
set /p FILE=Enter file name to encrypt: 
set /p PASSWORD=Enter password: 
java -jar coder.jar -ep "%FILE%" "%PASSWORD%"
pause