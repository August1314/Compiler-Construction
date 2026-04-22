@echo off
cd src
if exist ..\bin\parser\*.class del /q ..\bin\parser\*.class
javac -d ..\bin -classpath ..\bin parser\*.java
cd ..
pause
@echo on
