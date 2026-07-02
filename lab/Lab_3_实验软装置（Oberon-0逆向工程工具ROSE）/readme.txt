ROSE — Oberon-0 逆向工程工具 总自述文件
=========================================

一、基本信息
------------
姓名：梁力航
学号：23336128
电子邮件：153159571+August1314@users.noreply.github.com
联系电话：未提供
完成日期：2026-05-20

二、项目简介
------------
ROSE (Reverse Oberon Software Engineering) 是一个面向 Oberon-0 语言的
逆向工程工具，根据输入的 Oberon-0 源程序自动绘制设计图：
  - 子程序调用关系图（Call Graph）
  - 程序控制流设计图（Flowchart）

三、目录结构
------------
ROSE/ex1/          实验一：熟悉 Oberon-0 语言定义
  testcases/        正确源程序(.obr) + 变异程序(.001-.016)
  Oberon-0.md       实验报告（语言特点、二义性讨论）
  readme.txt

ROSE/ex2/          实验二：自动生成词法分析程序（JFlex）
  src/oberon.flex   JFlex 输入源文件
  src/OberonScanner.java  手写词法分析程序
  src/exceptions/   预定义异常类层次（16个异常类）
  lexgen.md         词汇表、正则定义式、lex工具比较
  build.sh         编译脚本
  run.sh           运行脚本
  test.sh          测试脚本
  readme.txt

ROSE/ex3/          实验三：自动生成语法分析程序（JavaCUP）
  src/oberon.cup    JavaCUP 输入源文件
  src/sym.java      Token 符号常量
  src/Parser.java   LALR 语法分析和语法制导翻译程序
  yaccgen.md        JavaCUP vs Bison 比较
  build.sh / run.sh / test.sh
  readme.txt

ROSE/ex4/          实验四：手工编写递归下降预测分析程序
  src/OberonParser.java  递归下降语法分析程序
  scheme.md         翻译模式设计文档
  build.sh / run.sh / test.sh
  readme.txt

ROSE/lib/          预定义设计图 API
  callgraph.jar     调用图绘制 API
  flowchart.jar     流程图绘制 API
  jgraph.jar        图形渲染库（JGraph）

ROSE/src/          ROSE 软装置 Demo 程序
  CallGraphDemo1.java / CallGraphDemo2.java
  FlowchartDemoAccount.java / FlowchartDemoAll.java
  FlowchartDemoOberon.java

ROSE/doc/          ROSE 软装置 Javadoc 文档

ROSE/report.md     实验心得总结
ROSE/readme.txt    本总自述文件

四、构建与运行
--------------
macOS / Linux:

# 实验二：词法分析器
cd ex2
./build.sh
./test.sh
./run.sh ../ex1/testcases/Sample.obr

# 实验三：语法分析器 + 调用图
cd ex3
./build.sh
./test.sh
./run.sh ../ex1/testcases/Sample.obr

# 实验四：递归下降 + 流程图
cd ex4
./build.sh
./test.sh
./run.sh ../ex1/testcases/Sample.obr

五、测试结果
------------
实验一：1 个正确 Oberon-0 源程序 + 16 个变异程序
实验二：词法错误检测 6/6 通过
实验三：语法/语义错误检测 7/7 通过，正确程序解析成功
实验四：错误检测通过，流程图生成正常

六、环境
--------
Java：OpenJDK 11.0.24 LTS (Corretto)
平台：macOS (arm64)
