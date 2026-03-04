@echo off
REM Build script for PersonalTaxCalculator
javac -d bin src\PersonalTaxCalculator.java
if errorlevel 1 goto error
echo Build successful.
goto end

:error
echo Build failed! Check for compilation errors.
exit /b 1

:end
pause
