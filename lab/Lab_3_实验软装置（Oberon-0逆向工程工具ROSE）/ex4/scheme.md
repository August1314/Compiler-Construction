# 实验四：Oberon-0 语言的翻译模式设计

**姓名**：梁力航
**学号**：23336128
**日期**：2026-05-20

---

## 1. 翻译模式概述

翻译模式（Translation Scheme）是在上下文无关文法中嵌入语义动作的程序设计方法。本实验为 Oberon-0 语言设计翻译模式，生成流程图（Flowchart）。

## 2. 文法转换

为适应递归下降预测分析，对原始 EBNF 文法做了以下转换：

### 2.1 消除左递归

```
原始：simple_expression → term | simple_expression + term | simple_expression - term
转换：simple_expression → term {+ term | - term}*
```

### 2.2 提取左公因子

Oberon-0 的文法已经天然避免了大部分左公因子问题。对于 statement 规则，通过向前看符号（lookahead）区分：

```
statement → IDENTIFIER (assignment_tail | procedure_call_tail)
           | IF expression THEN ...
           | WHILE expression DO ...
```

## 3. 翻译模式设计表

| 非终结符 | 继承属性 | 综合属性 | 语义动作 |
|---------|---------|---------|---------|
| module | - | Module对象 | 创建Module，添加Procedures，调用show() |
| procedure_declaration | Module对象 | - | 创建Procedure对象，添加到Module |
| statement_sequence | Procedure对象 | - | 按序解析statements，添加到Procedure |
| if_statement | - | IfStatement对象 | 创建IfStatement，填充true/false分支 |
| while_statement | - | WhileStatement对象 | 创建WhileStatement，填充循环体 |
| expression | - | String(文本) | 重建表达式文本用于流程图显示 |

## 4. 递归子程序映射

| 文法非终结符 | 递归子程序方法 |
|------------|-------------|
| module | parseModule() |
| declarations | parseDeclarations() |
| procedure_declaration | parseProcedureDeclaration() |
| statement_sequence | parseStatementSequence(Procedure) |
| statement | parseStatement(Procedure) |
| if_statement | parseIfStatement(Procedure) |
| while_statement | parseWhileStatement(Procedure) |
| expression | parseExpressionText() → String |
| simple_expression | parseTermText() 循环 |
| term | parseFactorText() 循环 |
| factor | parseFactorText() → String |
| type | parseType() → String |

## 5. 自顶向下 vs 自底向上 — 通过本实验的体会

通过实验三（JavaCUP / LALR）和实验四（递归下降）两个实验的亲身实践，对两种分析策略有了较深体会。

### 5.1 分析技术的简单性

| 方面 | 递归下降（自顶向下） | LALR（自底向上） |
|------|-------------------|-----------------|
| 程序结构 | 每个非终结符对应一个方法，直观映射 | 需要理解状态机、移进/归约逻辑 |
| 调试难度 | 低——可在方法入口打断点 | 高——分析表驱动，难以单步跟踪 |
| 文法转换 | 需要手工消除左递归、提取左公因子 | 不需要——LR分析天然处理左递归 |
| 学习曲线 | 平缓——从文法到代码的映射很自然 | 陡峭——需要理解LR项集、分析表构造 |

**体会**：对于 Oberon-0 这种文法简洁的语言，递归下降的简单性优势很明显。但从 EBNF 到代码的转换中，左递归消除和左公因子提取是必须小心的步骤，否则会导致死循环或选择分支错误。

### 5.2 分析技术的通用性

- **递归下降（LL）**：只能处理 LL(k) 文法。Oberon-0 的文法设计恰好天然适合 LL 分析（无左递归、无复杂二义性），所以几乎没有遇到文法限制问题
- **LALR(1)**：能处理更广范围的上下文无关文法，对文法书写者的要求更低——不需要特别考虑"这个规则会不会导致回溯"

### 5.3 语义动作的嵌入

- **递归下降**：语义动作可嵌入在解析过程的**任意位置**——在匹配关键字之前、解析子表达式之间、甚至在条件分支内部执行。这使得语法制导翻译非常自然
- **LALR**：语义动作只能在产生式**归约时**执行。对于需要在解析中间阶段执行动作的场景（如调用图注册在解析到过程名的瞬间完成），需要额外设计属性和归约时序

**体会**：在生成流程图时，递归下降的优势体现得特别明显——在解析 `IF` 关键字后就能立刻创建 `IfStatement` 对象，然后递归解析 true-branch 直接填充。而在 LALR 模式下，类似操作需要等到整条 if-statement 规则归约时才能完成。

### 5.4 错误恢复

- **递归下降**：可在方法级别实现 panic-mode 错误恢复——跳过当前输入直到遇到同步符号（如 `END`、`;`），然后继续解析
- **LALR**：错误恢复需要额外设计 error 产生式，或在分析表中插入 error 条目。这在 JavaCUP 中可以通过 `error` 终结符实现，但不如递归下降灵活

### 5.5 分析效率

- 两者理论时间复杂度均为 **O(n)**，其中 n 为输入长度
- 递归下降无表查找开销，但函数调用有栈帧开销
- LALR 有固定的状态表查找，但避免了递归调用
- 对于 Oberon-0 这种小语言，实测性能差异不明显

### 5.6 总结对比表

| 维度 | 递归下降（本实验四） | LALR（实验三） |
|------|-------------------|---------------|
| 分析方向 | 自顶向下 | 自底向上 |
| 简单性 | ★★★★★ 直观易调试 | ★★★☆☆ 需理解分析表 |
| 通用性 | ★★★☆☆ 需文法转换 | ★★★★★ 处理更广文法 |
| 语义动作 | ★★★★★ 任意位置嵌入 | ★★★☆☆ 仅在归约时 |
| 错误恢复 | ★★★★☆ 方法级恢复 | ★★★☆☆ 需特殊机制 |
| 分析速度 | ★★★★★ 无表开销 | ★★★★☆ 有表查找 |
| 表格大小 | ★★★★★ 无需分析表 | ★★★☆☆ 可能较大 |
| 适合场景 | 教学、手写解析器 | 工业级、自动生成 |

## 6. 参考资料

1. N. Wirth. *Theory and Techniques of Compiler Construction: An Introduction*. 1996.
2. A. Aho et al. *Compilers: Principles, Techniques, and Tools*, 2nd Ed. 2006.
