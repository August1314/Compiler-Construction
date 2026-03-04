@echo off
chcp 65001 >nul

@echo Running Testcase 001: Normal income tax calculation (50,000 yuan)
@echo ==============================================
@echo The input is:
type testcases\tc-001.in
@echo ----------------------------------------------
cd bin

rem : Run the testcase with input redirection
java PersonalTaxCalculator < ..\testcases\tc-001.in

rem : Compare the expected output
@echo ----------------------------------------------
@echo The expected output should be similar to: 
type ..\testcases\tc-001.out
@echo ----------------------------------------------
@echo Please verify the tax calculation:
@echo   - Taxable income: 45,000 yuan
@echo   - Tax amount: 9,090 yuan

cd ..
@echo ==============================================
pause
@echo on
