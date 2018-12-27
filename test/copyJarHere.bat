set jarFile=coder.jar
echo f | xcopy /y ..\build\libs\%jarFile% .\%jarFile%
REM pause