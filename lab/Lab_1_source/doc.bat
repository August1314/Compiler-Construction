@echo off
if not exist doc mkdir doc
javadoc -d doc -private src\*.java
pause
