@echo off
chcp 65001 >nul

echo ============================================
echo 测试用例 2: 低于起征点收入
echo ============================================
echo 测试场景：月工资 3,000 元（低于起征点 5,000 元）
echo 预期结果：
echo   - 未达到起征点，无需纳税
echo ============================================
echo.

REM 检查编译状态
if not exist "bin\PersonalTaxCalculator.class" (
    echo 错误：程序未编译，请先运行 build.bat
    pause
    exit /b 1
)

REM 创建测试用例目录
mkdir testcases 2>nul

REM 创建输入文件
echo 1> testcases\tc-002.in
echo 3000>> testcases\tc-002.in
echo 4>> testcases\tc-002.in

REM 创建预期输出文件
echo 正在启动个人所得税计算器...> testcases\tc-002.out
echo.>> testcases\tc-002.out
echo 功能菜单：>> testcases\tc-002.out
echo   1. 计算个人所得税>> testcases\tc-002.out
echo   2. 查看税率表>> testcases\tc-002.out
echo   3. 历史记录>> testcases\tc-002.out
echo   4. 退出程序>> testcases\tc-002.out
echo 请输入您的选择：>> testcases\tc-002.out
echo.>> testcases\tc-002.out
echo --- 计算个人所得税 --- >> testcases\tc-002.out
echo 请输入您的月工资薪金总额（元）：>> testcases\tc-002.out
echo.>> testcases\tc-002.out
echo 工资薪金总额：3000.00 元>> testcases\tc-002.out
echo 您的收入未达到起征点，无需纳税！>> testcases\tc-002.out
echo.>> testcases\tc-002.out
echo 功能菜单：>> testcases\tc-002.out
echo   1. 计算个人所得税>> testcases\tc-002.out
echo   2. 查看税率表>> testcases\tc-002.out
echo   3. 历史记录>> testcases\tc-002.out
echo   4. 退出程序>> testcases\tc-002.out
echo 请输入您的选择：>> testcases\tc-002.out
echo.>> testcases\tc-002.out
echo 程序已退出，再见！>> testcases\tc-002.out

echo 开始测试...
echo.

REM 运行测试
cd bin
java PersonalTaxCalculator < ..\testcases\tc-002.in > ..\testcases\tc-002-actual.out
cd ..

echo 测试实际输出：
type testcases\tc-002-actual.out
echo.

echo 预期输出：
type testcases\tc-002.out
echo.

REM 比较输出结果
fc testcases\tc-002-actual.out testcases\tc-002.out >nul
if %errorlevel% equ 0 (
    echo [PASS] 测试通过：输出与预期一致
) else (
    echo [FAIL] 测试失败：输出与预期不一致
)

REM 清理临时文件
del testcases\tc-002-in.txt >nul 2>&1
del testcases\tc-002-actual.out >nul 2>&1

echo.
echo ============================================
echo 测试用例 2 执行完毕
echo ============================================
pause
@echo off
chcp 65001 >nul

echo ============================================
echo 测试用例 2: 低于起征点收入
echo ============================================
echo 测试场景：月工资 3,000 元（低于起征点 5,000 元）
echo 预期结果：
echo   - 未达到起征点，无需纳税
echo ============================================
echo.

REM 检查编译状态
if not exist "bin\PersonalTaxCalculator.class" (
    echo 错误：程序未编译，请先运行 build.bat
    pause
    exit /b 1
)

echo 开始测试...
echo.

REM 使用临时文件保存测试结果
set TEST_RESULT=%TEMP%\tax_test_002.txt

REM 模拟用户输入：选择功能 1(计算个税), 输入工资 3000, 选择功能 4(退出)
(
echo 1
echo 3000
echo 4
) | java -cp bin PersonalTaxCalculator > %TEST_RESULT%

echo 测试输出：
type %TEST_RESULT%
echo.

REM 验证结果
findstr /C:"您的收入未达到起征点，无需纳税！" %TEST_RESULT% >nul
if %errorlevel% equ 0 (
    echo [PASS] 正确识别未达起征点情况
) else (
    echo [FAIL] 未达起征点判断错误
)

findstr /C:"工资薪金总额：3000.00 元" %TEST_RESULT% >nul
if %errorlevel% equ 0 (
    echo [PASS] 工资总额显示正确
) else (
    echo [FAIL] 工资总额显示错误
)

REM 清理临时文件
del %TEST_RESULT% >nul 2>&1

echo.
echo ============================================
echo 测试用例 2 执行完毕
echo ============================================
pause
