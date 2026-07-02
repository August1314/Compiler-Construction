# ROSE — Oberon-0 逆向工程工具 实验心得

**姓名**：梁力航
**学号**：23336128
**课程**：DCS292 编译器构造实验
**日期**：2026-05-20

---

## 1. 实验概览

ROSE 项目是一个综合型编译器构造实验，要求开发面向 Oberon-0 语言的逆向工程工具，根据源程序自动生成调用图（Call Graph）和流程图（Flowchart）。项目分解为四个子实验，层层递进：

- **实验一**：熟悉 Oberon-0 语言定义，编写测试用例
- **实验二**：使用 JFlex 自动生成词法分析程序
- **实验三**：使用 JavaCUP 自动生成语法分析程序 + 调用图生成
- **实验四**：手工编写递归下降预测分析程序 + 流程图生成

## 2. 四个实验的递进关系

```
实验一（语言理解）
  → 理解 Oberon-0 的词法/语法/语义
  → 产出：测试用例（正确+变异）
      ↓
实验二（词法分析）
  → 将字符序列转为 token 流
  → 产出：OberonScanner（手写 DFA 模拟 JFlex 生成）
      ↓
实验三（语法分析 + 调用图）
  → LALR 自底向上解析，语义动作生成调用图
  → 产出：Parser + CallGraph
      ↓
实验四（语法分析 + 流程图）
  → 递归下降自顶向下解析，翻译模式生成流程图
  → 产出：OberonParser + Flowchart
```

每个实验既是独立交付物，又为后续实验提供基础。实验二的异常类被实验三/四复用，实验一的测试用例贯穿所有实验。

## 3. 各环节核心体验

### 3.1 词法分析

编写 OberonScanner 的过程让我深刻理解了 DFA 的工作原理：
- 状态转换与 token 类型的映射关系
- 向前看（lookahead）在区分 `:=` vs `:`、`<=` vs `<` 中的作用
- 词法错误的检测时机——有些错误（如非法字符）在扫描时立即报告，有些（如括号不匹配）留待语法分析阶段处理

Oberon-0 的大小写无关特性简化了符号表管理，但也要求扫描器对所有保留字和关键字做 `toLowerCase()` 统一处理。

### 3.2 语法分析

两个实验分别实现了 LALR 和递归下降两种策略：

- **LALR（实验三）**：从 BNF 到 JavaCUP 规格的转换相对机械，但调试冲突（shift/reduce）时需要对 LR 分析有深入理解。Oberon-0 的简单文法避免了大多数冲突
- **递归下降（实验四）**：将 EBNF 直接映射为递归方法，直观但需要处理左递归消除和向前看符号的正确选择

两种策略在 Oberon-0 上的实现都可行，但递归下降在语义动作嵌入（流程图生成）上明显更灵活。

### 3.3 语法制导翻译

这是整个 ROSE 项目的核心——在解析过程中嵌入语义动作：
- 调用图：在解析到过程声明时注册节点，在解析到过程调用时添加边
- 流程图：在解析到语句时创建对应的图形组件（PrimitiveStatement / IfStatement / WhileStatement）

通过 ROSE 软装置的 CallGraph 和 Flowchart API，生成的图可以直接通过 JGraph 可视化展示。

### 3.4 错误处理

Oberon-0 的错误处理遵循分层策略：
- 词法层：6 种词法错误（非法符号、整数格式、标识符长度等）
- 语法层：4 种语法错误（括号不匹配、缺少运算符/操作数）
- 语义层：2 种语义错误（类型不匹配、参数不匹配）

每种错误通过独立的异常类标识，便于上层程序根据错误类型采取不同处理策略。

## 4. 面向对象设计体会

在 ROSE 项目中，OO 设计体现在以下方面：

1. **异常类层次**：OberonException → LexicalException / SyntacticException / SemanticException → 具体异常类。清晰的继承关系使得错误分类和处理变得简洁
2. **Scanner 与 Parser 的解耦**：Scanner 只负责 token 化，Parser 只负责语法分析和语义动作，通过 Token 接口通信
3. **API 复用**：CallGraph 和 Flowchart API 封装了 JGraph 的复杂性，让我可以专注于语法分析和语义动作的设计
4. **递归子程序模式**：在实验四中，每个非终结符对应一个方法，方法的参数传递继承属性，返回值传递综合属性——这是面向对象语言中实现翻译模式的自然方式

## 5. 总结

通过 ROSE 项目的四个实验，我完整经历了一个小型编译器的前端构建过程：

1. 理解了词法分析的 DFA 原理和自动生成工具的使用
2. 掌握了 LALR 和递归下降两种语法分析策略
3. 实践了语法制导翻译，将语义动作嵌入解析过程
4. 学习了异常处理在编译器错误报告中的应用
5. 体会了自动生成工具（JFlex/JavaCUP）与手工编写各自的优劣

整个项目让我对编译原理课堂上学到的理论知识有了实践层面的验证和深化，也使我对"如何设计一门可解析的程序设计语言"有了更直观的认识。

---

## 附录：项目文件组织

```
Lab_3_ROSE/
├── ex1/          实验一：Oberon-0 语言定义
│   ├── readme.txt
│   ├── Oberon-0.md        实验报告
│   └── testcases/         正确程序(.obr) + 变异程序(.001-.016)
├── ex2/          实验二：JFlex 词法分析
│   ├── readme.txt
│   ├── lexgen.md          词法规则报告
│   ├── build.sh / run.sh / test.sh / gen.sh
│   └── src/
│       ├── oberon.flex    JFlex 输入源文件
│       ├── OberonScanner.java
│       └── exceptions/    异常类层次
├── ex3/          实验三：JavaCUP 语法分析 + 调用图
│   ├── readme.txt
│   ├── yaccgen.md         yacc 工具比较报告
│   ├── build.sh / run.sh / test.sh / gen.sh
│   └── src/
│       ├── oberon.cup     JavaCUP 输入源文件
│       ├── sym.java       Token 符号常量
│       └── Parser.java    语法+LALR解析器
├── ex4/          实验四：递归下降 + 流程图
│   ├── readme.txt
│   ├── scheme.md          翻译模式设计报告
│   ├── build.sh / run.sh / test.sh
│   └── src/
│       └── OberonParser.java
├── report.md     本实验心得报告
└── readme.txt    项目总自述文件
```
