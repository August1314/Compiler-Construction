# 实验一：熟悉 Oberon-0 语言定义

**姓名**：梁力航
**学号**：23336128
**日期**：2026-05-20

---

## 1. Oberon-0 语言特点

### 1.1 保留字 (Reserved Word) 与关键字 (Keyword) 的区别

Oberon-0 继承了 Pascal 语言的传统，严格区分保留字和关键字两个概念：

- **保留字 (Reserved Word)**：在语言中被语法规则所保留，程序员不能将其声明为标识符使用。Oberon-0 的保留字包括：`MODULE`、`BEGIN`、`END`、`CONST`、`TYPE`、`VAR`、`PROCEDURE`、`IF`、`THEN`、`ELSIF`、`ELSE`、`WHILE`、`DO`、`ARRAY`、`OF`、`RECORD`、`DIV`、`MOD`、`OR`、`INTEGER`、`BOOLEAN`、`TRUE`、`FALSE` 等。

- **关键字 (Keyword)**：在语言中具有特定含义的预定义标识符，但在语法上它们仍然是标识符，可以被程序员重新声明覆盖。Oberon-0 的关键字包括：`WRITE`、`WRITELN`、`READ` 等预定义过程名。

这种区分的意义在于：
- 保留字是语法结构的一部分，词法分析器必须将其识别为独立的 token 类型
- 关键字是预定义的标识符，遵循标识符的作用域规则
- 程序员可以在内层作用域中重新声明关键字（虽然实践中不推荐）

### 1.2 Oberon-0 表达式语法与 Java/C/C++ 的差异

| 特性 | Oberon-0 | Java / C / C++ |
|------|----------|----------------|
| 运算符优先级 | 仅 4 层（比较 > 加减OR > 乘除MOD DIV & > 取反） | 15+ 层优先级 |
| 逻辑与 | `&`（无短路求值） | `&&`（短路求值） |
| 逻辑或 | `OR`（无短路求值） | `\|\|`（短路求值） |
| 取反 | `~` | `!` |
| 不等号 | `#` | `!=` |
| 整除 | `DIV` | `/`（整数除法时） |
| 取模 | `MOD` | `%` |
| 关系运算链 | 不支持（`a = b` 产生 BOOLEAN，不能再比较） | C 支持（但语义不同） |
| 算术类型 | 仅 INTEGER | int/long/float/double 等 |
| 布尔常量 | 不支持 TRUE/FALSE 字面量 | 支持 true/false |

关键差异：
1. Oberon-0 不允许 BOOLEAN 类型参与关系比较或算术运算，保证了类型安全
2. Oberon-0 的表达式语法更简单，避免了 C 语言中 `=` 与 `==` 混淆的问题

---

## 2. Oberon-0 文法二义性讨论

### 2.1 文法分析

Oberon-0 语言的 EBNF 文法定义**不存在二义性**。原因如下：

1. **表达式文法的分层设计**：Oberon-0 的表达式文法通过非终结符分层（expression → simple_expression → term → factor），隐式地编码了运算符优先级和结合性，消除了常见语言中中缀运算符的二义性。

2. **IF 语句的闭括号 END**：传统语言中的 "dangling else" 问题在 Oberon-0 中不存在，因为每个 IF 语句必须以 `END` 关键字结束，消除了嵌套 IF 语句的二义性。

```
(* 在 C 语言中：*)
if (a > 0) if (b > 0) x = 1; else x = 2;  (* ELSE 属于哪个 IF？*)

(* 在 Oberon-0 中：无歧义 *)
IF a > 0 THEN
  IF b > 0 THEN x := 1 END
ELSE
  x := 2
END
```

3. **选择符的后缀语法**：选择符 `.field` 和 `[index]` 采用后缀形式，自然地从左到右结合，不存在前缀/后缀运算符混合导致的二义性。

4. **无空语句问题**：每个 statement 和 statement_sequence 都有明确的语法边界，不存在 "空语句" 导致的歧义。

### 2.2 为何其他语言的常见二义性问题在 Oberon-0 中不存在

| 常见二义性 | 典型语言 | Oberon-0 的解决方案 |
|-----------|---------|-------------------|
| Dangling ELSE | C, Java, Pascal | IF 必须以 END 关闭 |
| 运算符优先级 | C (15级) | 仅 4 级，EBNF 分层消除歧义 |
| 前缀/后缀歧义 | C (++, --, *) | 无语义相同的前缀/后缀运算符 |
| 声明与表达式歧义 | C++ (Most Vexing Parse) | 语法设计严格区分 declaration 和 statement |

---

## 3. 实验心得体会

通过本实验，我对 Oberon-0 语言有了深入理解：
- Oberon-0 虽小却功能完备，涵盖了模块化、结构化控制流、子程序、参数传递等核心概念
- 其文法设计精妙，通过简单的 EBNF 规则消除了许多常见语言中的二义性
- 语言设计中的一些选择（如 IF 必须以 END 关闭、严格的类型系统）体现了 Wirth 的语言设计哲学：简洁、安全、无歧义

---

## 4. 参考资料

1. N. Wirth. *Theory and Techniques of Compiler Construction: An Introduction*. Addison-Wesley, 1996.
2. ISO/IEC 14977:1996(E). The Standard Metalanguage Extended BNF.
3. A. Aho et al. *Compilers: Principles, Techniques, and Tools*, 2nd Ed. Addison-Wesley, 2006.
