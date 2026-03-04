@echo off
@echo Running Testcase 004: Boundary value test (5,000 yuan = threshold)
@echo ==============================================
@echo The input is:
type testcases\tc-004.in
@echo ----------------------------------------------
cd bin

rem : Run the testcase with input redirection
java PersonalTaxCalculator < ..\testcases\tc-004.in

rem : Compare the expected output
@echo ----------------------------------------------
@echo The expected output should be similar to: 
type ..\testcases\tc-004.out
@echo ----------------------------------------------
@echo Please verify: No tax required when income equals threshold

cd ..
@echo ==============================================
pause
@echo on
