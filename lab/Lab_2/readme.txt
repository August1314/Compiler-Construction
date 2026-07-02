ExprEval Lab2 自述文件
======================

一、基本信息
------------
姓名：梁力航
学号：23336128
电子邮件：153159571+August1314@users.noreply.github.com
联系电话：未提供
完成日期：2026-04-22

二、目录说明
------------
src/parser    自行实现的词法分析、算符优先语法分析和语义处理代码
bin           编译后的软装置类文件
doc           由 javadoc 生成的 API 文档
testcases     simple.xml、standard.xml、mytest.xml 测试用例
design.pdf    按实验 PDF 要求整理的正式实验报告

三、主要命令
------------
Windows:
build.bat         编译 parser 包
run.bat           启动 GUI
test_simple.bat   运行 simple.xml
test_standard.bat 运行 standard.xml
test_my.bat       运行 mytest.xml
doc.bat           生成 javadoc 文档

Linux / macOS:
build.sh          编译 parser 包
run.sh            启动 GUI
test.sh           运行测试用例
doc.sh            生成 javadoc 文档
build_report.sh   编译 LaTeX 报告

四、实现说明
------------
1. 对外接口保持为 parser.Calculator#calculate(String)。
2. 内部实现改为 Scanner + Parser + OperatorTable + Value 的 OPP 结构。
3. 词法、语法、语义阶段只抛各自层级的异常及其派生类。
4. 已补充 mytest.xml，并补齐 design.pdf 与实验报告截图。

五、补充说明
------------
1. standard.xml 测试通过 16 / 16。
2. mytest.xml 测试通过 15 / 15。
3. 报告源文件为 report.tex，正式提交文件为 design.pdf。
