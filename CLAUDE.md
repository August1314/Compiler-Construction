# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 仓库定位

这是一个《编译器构造》课程仓库，不是单一应用。根目录下主要有三类内容：

- `lab/`：实验代码与提交材料。**默认优先关注 `lab/Lab_1_source`**，这是仓库里最完整、最适合继续开发的代码项目。
- `PersonalTaxCalc1/`：独立的 Java 命令行练习项目，与编译器实验无直接耦合。
- `lec/`：讲义、作业、思维导图、PDF 等课程资料，通常不是主开发目录。

如果任务没有明确说明目录：
- 与“后缀表达式 / parser / Lab1 / 编译器实验”相关：进入 `lab/Lab_1_source/`
- 与“个人所得税计算器”相关：进入 `PersonalTaxCalc1/`

## 常用命令

### `lab/Lab_1_source`（编译器实验主项目）

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

- `lab/Lab_1_source/`：Lab1 后缀表达式实验，包含源码、测试、性能比较、Javadoc、HTML/PDF 报告。
- `PersonalTaxCalc1/`：单文件 Java CLI 程序，配套脚本、Javadoc、测试样例和设计文档。
- `lec/`：课程资料，不要把这里的 PDF/作业文件当作主代码入口。

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
- 根目录 `.gitignore` 已忽略常见生成物，如 `bin/`、`build/`、`out/`、`tmp/`、`.DS_Store` 等；新增文件时尽量不要提交生成产物。
- 仓库中有大量 PDF、HTML、ZIP、Javadoc 产物与课程资料。除非任务明确要求，否则优先修改源码和测试，不要编辑导出物。
