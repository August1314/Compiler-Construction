# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 仓库定位

这是一个《编译器构造》课程仓库，不是单一应用。根目录下主要有以下内容：

- `lab/`：实验代码与提交材料，包含 Lab_0 ~ Lab_3。
- `PersonalTaxCalc1/`：独立的 Java 命令行练习项目，与编译器实验无直接耦合。
- `lec/`：讲义（第 1-7 周）、作业、思维导图、PDF 等课程资料，通常不是主开发目录。

如果任务没有明确说明目录：
- 与"后缀表达式 / parser / Lab1 / 编译器实验"相关：进入 `lab/Lab_1_source/`
- 与"表达式计算器 / ExprEval / OPP / 算符优先 / Lab2"相关：进入 `lab/Lab_2/`
- 与"Oberon-0 / 逆向工程 / ROSE / 调用图 / 流程图 / Lab3"相关：进入 `lab/Lab_3_实验软装置（Oberon-0逆向工程工具ROSE）/`
- 与"个人所得税计算器"相关：进入 `PersonalTaxCalc1/`

## 常用命令

### `lab/Lab_1_source`（Lab1 — 后缀表达式解析器）

先进入目录：

```bash
cd /Users/lianglihang/Downloads/Compiler-Construction/lab/Lab_1_source
```

构建：

```bash
./build.sh
```

运行主程序：

```bash
./run.sh
```

启用错误恢复运行：

```bash
./run.sh --recover
```

运行全部测试（JUnit + 样例回归）：

```bash
./test.sh
```

生成 Javadoc：

```bash
./doc.sh
```

运行性能比较：

```bash
./perf.sh
```

运行单个样例测试：

```bash
java -cp bin Postfix --quiet < testcases/tc-001.infix
```

仅运行 JUnit 测试类：

```bash
javac -cp "lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar:bin" -d test-classes test/PostfixTest.java && java -cp "test-classes:bin:lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar" org.junit.runner.JUnitCore PostfixTest
```

性能脚本支持附加参数：

```bash
java -cp bin PerfCompare --rounds 5 --count 2000 --length 64 --csv
```

### `lab/Lab_2`（Lab2 — ExprEval 算符优先计算器）

先进入目录：

```bash
cd /Users/lianglihang/Downloads/Compiler-Construction/lab/Lab_2
```

构建（编译 `src/parser/*.java` 到 `bin/`）：

```bash
./build.sh
```

运行 GUI：

```bash
./run.sh
```

运行全部测试（simple.xml + standard.xml）：

```bash
./test.sh
```

运行单个测试：

```bash
./test.sh testcases/mytest.xml
```

生成 Javadoc：

```bash
./doc.sh
```

编译 LaTeX 报告（需要 LaTeX 环境）：

```bash
./build_report.sh
```

### `lab/Lab_3_实验软装置（Oberon-0逆向工程工具ROSE）`（Lab3 — ROSE 逆向工程）

先进入目录：

```bash
cd "/Users/lianglihang/Downloads/Compiler-Construction/lab/Lab_3_实验软装置（Oberon-0逆向工程工具ROSE）"
```

该项目仅有 Windows `.bat` 脚本，macOS 下需手动编译运行。例如：

```bash
javac -cp "lib/callgraph.jar:lib/flowchart.jar:lib/jgraph.jar" -d bin src/CallGraphDemo1.java
java -cp "bin:lib/callgraph.jar:lib/flowchart.jar:lib/jgraph.jar" CallGraphDemo1
```

各 Demo 入口：
- `CallGraphDemo1` / `CallGraphDemo2` — 调用图示例
- `FlowchartDemoAccount` / `FlowchartDemoAll` / `FlowchartDemoOberon` — 流程图示例

### `PersonalTaxCalc1`（独立 Java CLI 项目）

先进入目录：

```bash
cd /Users/lianglihang/Downloads/Compiler-Construction/PersonalTaxCalc1
```

构建（会同时生成 Javadoc）：

```bash
./build.sh
```

运行程序：

```bash
./run.sh
```

在 macOS/Linux 下运行单个样例输入：

```bash
java -cp bin PersonalTaxCalculator < testcases/tc-001.in
```

该项目仓库内未提供对应的 Unix 测试总脚本；现有回归测试脚本主要是 Windows `.bat`：

```bat
testcase.bat
testcase-001.bat
```

### 其他目录

`lec/作业/1/assignment1/` 是单独的 LaTeX 作业目录：

```bash
cd /Users/lianglihang/Downloads/Compiler-Construction/lec/作业/1/assignment1
make
make clean
```

## 代码架构

### 顶层结构

- `lab/Lab_0/`：仅文档（Markdown + PDF 实验说明），无可编译代码。
- `lab/Lab_1_source/`：Lab1 后缀表达式实验，包含源码、测试、性能比较、Javadoc、HTML/PDF 报告。
- `lab/Lab_2/`：Lab2 表达式计算器（ExprEval）OPP 改写，完整工程化子项目。
- `lab/Lab_3_实验软装置（Oberon-0逆向工程工具ROSE）/`：Lab3 Oberon-0 逆向工程工具，使用预编译 JAR 库。
- `PersonalTaxCalc1/`：单文件 Java CLI 程序，配套脚本、Javadoc、测试样例和设计文档。
- `lec/`：课程资料（第 1-7 周讲义 PDF + 部分 Markdown 笔记），不要把这里的 PDF/作业文件当作主代码入口。

### `lab/Lab_1_source` 架构

这是仓库里最完整的工程化子项目。

#### 关键源码

- `lab/Lab_1_source/src/Postfix.java`
  - `Postfix`：CLI 入口，负责解析 `--recover` / `--quiet`
  - `Parser`：核心解析器，按字符读取输入并输出后缀表达式
  - `ParseResult`：封装 postfix 结果和错误列表
  - `ParseError` / `ErrorKind`：错误分类与定位
- `lab/Lab_1_source/src/PerfCompare.java`
  - `PerfCompare`：性能比较入口
  - `RecursiveParser`：保留的旧递归版解析器，仅用于和当前实现做性能对比
- `lab/Lab_1_source/test/PostfixTest.java`
  - JUnit 4 测试

#### 主执行流程

`Postfix.main` 从标准输入读取表达式，创建 `Parser` 后调用 `parse()`。

`Parser` 不是递归下降主实现，而是一个**状态机式迭代解析器**：
- `expectOperand`：当前是否期待操作数
- `pendingOp`：缓存最近读到的 `+`/`-`
- `position`：记录 1-based 错误位置
- `output`：仅在尚未出错时追加输出

输入语言很小：
- 终结符：单个数字、`+`、`-`
- 不允许空白字符
- 遇错时区分 `LEXICAL` 和 `SYNTAX`
- `--recover` 会继续扫描并收集更多错误

#### 测试与脚本关系

- `build.sh`：编译 `src/*.java` 到 `bin/`
- `run.sh`：运行 `Postfix`
- `test.sh`：**总测试入口**
  1. 构建主程序
  2. 编译并运行 `test/PostfixTest.java`
  3. 用 `--quiet` 跑 `testcases/*.infix`
  4. 将输出写到 `tmp/`
  5. 与 `testcases/*.postfix` 做 `diff`
- `doc.sh`：为 `src/*.java` 生成 Javadoc 到 `doc/`
- `perf.sh`：运行 `PerfCompare`

#### 目录使用约定

优先修改：
- `src/`
- `test/`
- `testcases/`

通常不要手改：
- `bin/`
- `doc/`
- `test-classes/`
- `tmp/`

### `lab/Lab_2` 架构

这是一个基于 ExprEval 软装置的**算符优先文法（OPP）改写项目**。

#### 学生自写部分（`src/parser/`）

- `Calculator.java` — 对外接口 `parser.Calculator#calculate(String)`，委托给 Scanner + Parser
- `Scanner.java` — 词法分析器，支持数字（含科学计数法）、布尔字面量、标识符（函数名）、运算符、括号、逗号、三元运算符。空白分隔，非法字符报错
- `Parser.java` — 算符优先移进/归约解析器，维护运算符栈 + 值栈 + 括号帧栈，支持单目负/非、二元算术/比较/逻辑、三元条件、函数调用（sin/cos/max/min）
- `OperatorTable.java` — 算符优先关系表，定义各运算符之间的 `<·` / `·>` / `=` 关系
- `Token.java` / `TokenType.java` — Token 数据结构与枚举（END, NUMBER, BOOLEAN, IDENTIFIER, 各类运算符，LPAREN, RPAREN, COMMA, QUESTION, COLON）
- `Value.java` — 运行时值封装，统一处理数字和布尔值

#### 预编译依赖（`bin/`，无源码）

- `bin/exceptions/` — 异常类层次（ExpressionException 及其 12 个子类）
- `bin/gui/` — Swing GUI 界面
- `bin/test/ExprEvalTest.class` — XML 驱动的测试框架
- `bin/ExprEval.class` — GUI 启动入口

#### 测试方式

测试用例为 XML 文件（`testcases/*.xml`），由 `test.ExprEvalTest` 加载运行。`test.sh` 支持可选参数指定单个文件。

#### 目录使用约定

优先修改：
- `src/parser/`
- `testcases/`
- `report.tex`（实验报告源文件）

通常不要手改：
- `bin/`（含第三方 class 文件）
- `doc/`
- `ref/`
- `report_assets/`

### `lab/Lab_3_实验软装置（Oberon-0逆向工程工具ROSE）` 架构

这是一个**预编译工具集**，用于 Oberon-0 编译器的逆向工程分析。

#### 关键组件

- `lib/callgraph.jar` — 调用图 API（CallGraph, Procedure, CallSite）
- `lib/flowchart.jar` — 流程图 API（Module, Procedure, WhileStatement, IfStatement 等）
- `lib/jgraph.jar` — 图形渲染库（JGraph）
- `src/` — 5 个 Demo 程序，演示如何使用上述两个 API 构建调用图和流程图

#### 特点

- 仓库内提供的只有 Demo 源码和 JAR 库，不需要学生修改库代码
- 实际实验任务是在 ROSE 工具中导入 Oberon-0 源码进行逆向分析，产出调用图和流程图
- 所有脚本均为 Windows `.bat`，macOS 下需手动拼接 javac/java 命令

### `PersonalTaxCalc1` 架构

这是一个独立的小型 Java 命令行项目，结构比 Lab1 简单很多。

#### 关键源码

- `PersonalTaxCalc1/src/PersonalTaxCalculator.java`
  - `PersonalTaxCalculator`：菜单循环、收入输入、起征点调整、税率修改
  - `TaxRate`：税率区间模型

#### 结构特征

- 几乎所有逻辑集中在一个源文件中
- 通过 `static` 字段和 `static` 方法维护程序状态
- 使用 `Scanner` 做交互输入
- 更像课程作业式 CLI，而不是模块化应用

#### 测试方式

- 测试样例位于 `PersonalTaxCalc1/testcases/`
- 测试说明在 `PersonalTaxCalc1/TEST_INSTRUCTIONS.txt`
- 主要依赖 Windows `.bat` 脚本进行回归测试
- 在 macOS/Linux 下，如需验证单个样例，直接用输入重定向运行 `java -cp bin PersonalTaxCalculator < testcases/tc-001.in`

## 开发注意事项

- 仓库没有统一的包管理器或顶层构建系统；按子项目分别运行脚本。
- 代码主要基于 Java，文档里明确的测试环境是 **OpenJDK 11.0.24 LTS**；代码本身尽量保持对较旧 Java 版本友好。
- `lab/Lab_1_source/lib/` 已内置 JUnit 4.13.2 和 Hamcrest 1.3，测试命令默认依赖它们。
- `lab/Lab_2/bin/` 中有大量预编译的第三方 class 文件（exceptions, gui, test, ExprEval），这些不是学生产物，不要修改或删除。
- `lab/Lab_3_实验软装置（Oberon-0逆向工程工具ROSE）/lib/` 中的 JAR 是预编译库，同样不要修改。
- 根目录 `.gitignore` 已忽略常见生成物，如 `bin/`、`build/`、`out/`、`tmp/`、`.DS_Store` 等；新增文件时尽量不要提交生成产物。
- 仓库中有大量 PDF、HTML、ZIP、Javadoc 产物与课程资料。除非任务明确要求，否则优先修改源码和测试，不要编辑导出物。
