@echo off
set key=.\key.key
set /p file=Enter file name to decrypt: 
java -jar coder.jar -df %key% %file%
pause