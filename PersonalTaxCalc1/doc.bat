@echo off
REM Generate Javadoc documentation
javadoc -d doc -private src\PersonalTaxCalculator.java
if errorlevel 1 goto error
echo Javadoc generation successful.
goto end

:error
echo Javadoc generation failed!
exit /b 1

:end
pause