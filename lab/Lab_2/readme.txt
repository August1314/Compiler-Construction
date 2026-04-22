Lab 2 提交说明
=================

1. 目录说明
src/parser    自行实现的词法分析、算符优先语法分析和语义处理代码
bin           编译后的软装置类文件
doc           由 javadoc 生成的 API 文档
testcases     simple.xml、standard.xml、mytest.xml 测试用例
design.pdf    按实验 PDF 要求整理的正式实验报告

2. Windows 下的主要命令
build.bat         编译 parser 包
run.bat           启动 GUI
test_simple.bat   运行 simple.xml
test_standard.bat 运行 standard.xml
test_my.bat       运行 mytest.xml
doc.bat           生成 javadoc 文档

3. Linux / macOS 下的辅助脚本
build.sh          编译 parser 包
run.sh            启动 GUI
test.sh           不带参数时运行 simple.xml 和 standard.xml；传入 testcases/mytest.xml 时仅运行该文件
doc.sh            生成 javadoc 文档
build_report.sh   编译 LaTeX 报告

4. 实现说明
对外接口保持为 parser.Calculator#calculate(String)。
内部实现改为 Scanner + Parser + OperatorTable + Value 的 OPP 结构。
词法、语法、语义阶段只抛各自层级的异常及其派生类。
