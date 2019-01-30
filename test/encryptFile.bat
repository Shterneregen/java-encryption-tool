@echo off
set key=.\key.pub
set /p file=Enter file name to encrypt: 
java -jar coder.jar -ef %key% %file%
pause