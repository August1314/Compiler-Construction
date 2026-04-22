# 中山大学计算机学院

# 《编译器构造实验》Lab2 说明

## 实验主题

Lab2 表达式求值器（ExprEval）。

本实验在原始软装置的基础上补全 `parser.Calculator` 的核心逻辑，实现：

- 词法分析：整数、浮点数、科学计数法、布尔常量、标识符、运算符与分隔符
- 语法分析：算术、关系、逻辑、三目、函数调用与括号嵌套
- 语义检查：数值/布尔类型约束、除零检测、函数参数校验
- 错误处理：按实验框架约定抛出对应异常类型

## 目录结构

- `src/parser/Calculator.java`：本次实验唯一需要提交的核心源码
- `bin/`：实验框架编译产物与运行入口
- `testcases/`：官方 XML 测试集
- `doc/`：Javadoc 文档
- `report.tex`：实验报告 LaTeX 源文件
- `Lab2 实验报告.pdf`：最终 PDF 报告

## 环境

- Java：OpenJDK 11.0.24 LTS
- 平台：macOS（兼容实验要求中的 Windows 批处理）

## 构建与运行

### macOS / Linux

```bash
./build.sh
./run.sh
./test.sh
./doc.sh
```

### Windows

```bat
build.bat
run.bat
test_simple.bat
test_standard.bat
doc.bat
```

## 当前结果

- `simple.xml`：8 / 8 通过
- `standard.xml`：16 / 16 通过
- 关键异常类型：`MissingLeftParenthesisException`、`MissingRightParenthesisException`、`MissingOperatorException`、`MissingOperandException`、`DividedByZeroException`、`TypeMismatchedException`、`IllegalDecimalException`、`IllegalIdentifierException`、`FunctionCallException`

## 实现要点

1. 使用单文件递归下降解析器，避免为实验额外引入复杂框架。
2. 采用显式 `Value` 类型区分 `double` 与 `boolean`，将类型错误前移到语义阶段。
3. 将词法分析与语法分析分离，优先保证异常类型与实验测试集完全对齐。

## 文档与报告

- `report.tex` 是当前正式报告源文件
- `Lab2 实验报告.pdf` 由 `xelatex` 编译生成
