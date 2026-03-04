@echo off
chcp 65001 >nul

echo Running Testcase 003: High income tax calculation (100,000 yuan)
echo ==============================================
echo The input is:
type testcases\tc-003.in
echo ----------------------------------------------
cd bin

rem : Run the testcase with input redirection
java PersonalTaxCalculator < ..\testcases\tc-003.in

rem : Compare the expected output
@echo ----------------------------------------------
@echo The expected output should be similar to: 
type ..\testcases\tc-003.out
@echo ----------------------------------------------
@echo Please verify the tax calculation:
@echo   - Taxable income: 95,000 yuan
@echo   - Tax amount: 26,440 yuan (verify manually)

cd ..
@echo ==============================================
pause
@echo on
