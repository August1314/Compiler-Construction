@echo off
if not exist bin call build.bat

if not exist lib\junit-4.13.2.jar (
  echo Missing lib\junit-4.13.2.jar
  echo Please download JUnit 4.13.2 and Hamcrest 1.3 into lib\
  pause
  exit /b 1
)
if not exist lib\hamcrest-core-1.3.jar (
  echo Missing lib\hamcrest-core-1.3.jar
  echo Please download JUnit 4.13.2 and Hamcrest 1.3 into lib\
  pause
  exit /b 1
)

if not exist test-classes mkdir test-classes
javac -cp lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar;bin -d test-classes test\PostfixTest.java

echo Running JUnit tests...
java -cp test-classes;bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar org.junit.runner.JUnitCore PostfixTest

echo.
echo Running sample testcases (quiet mode)...
if not exist tmp mkdir tmp
java -cp bin Postfix --quiet < testcases\tc-001.infix > tmp\tc-001.out 2>nul
java -cp bin Postfix --quiet < testcases\tc-002.infix > tmp\tc-002.out 2>nul
java -cp bin Postfix --quiet < testcases\tc-003.infix > tmp\tc-003.out 2>nul
java -cp bin Postfix --quiet < testcases\tc-004.infix > tmp\tc-004.out 2>nul

fc /b tmp\tc-001.out testcases\tc-001.postfix
fc /b tmp\tc-002.out testcases\tc-002.postfix
fc /b tmp\tc-003.out testcases\tc-003.postfix
fc /b tmp\tc-004.out testcases\tc-004.postfix

pause
@echo on
