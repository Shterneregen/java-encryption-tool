@echo off
set KEY=.\key.pub
set /p FILE=Enter file name to encrypt: 
java -jar coder.jar -ef %KEY% "%FILE%"
pause