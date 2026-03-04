@echo off
cd bin
java PersonalTaxCalculator
cd ..
pause
@echo on

echo === 运行个人所得税计算器 ===

REM 检查bin目录是否存在
if not exist "bin" (
    echo 错误：bin目录不存在，请先编译程序！
    echo 请运行 build.bat 编译程序
    pause
    exit /b 1
)

REM 检查主类是否存在
if not exist "bin\PersonalTaxCalculator.class" (
    echo 错误：PersonalTaxCalculator 类不存在，请先编译程序！
    echo 请运行 build.bat 编译程序
    pause
    exit /b 1
)

REM 运行程序
echo 启动个人所得税计算器...
java -cp bin PersonalTaxCalculator

echo === 程序运行结束 ===
pause
