# 实验三：JavaCUP 与 GNU Bison 差异比较

**姓名**：梁力航
**学号**：23336128
**日期**：2026-05-20

---

## 1. JavaCUP 与 GNU Bison 的关键差异

### 1.1 语法规则定义差异

| 特性 | JavaCUP | GNU Bison |
|------|---------|-----------|
| 输入文件格式 | `.cup` 文件 | `.y` 文件 |
| 声明的终结符 | `terminal SYMBOL1, SYMBOL2;` | `%token SYMBOL1 SYMBOL2` |
| 声明非终结符 | `non terminal NT;` | 不需要显式声明 |
| 语义动作类型 | `TYPED` label | `$$`, `$1`, `$2`, ... |
| 开始符号 | `start with symbol;` | `%start symbol` |
| 优先级声明 | `precedence left PLUS;` | `%left PLUS` |
| 嵌入代码块 | `parser code {: ... :}` | `%{ ... %}` |
| 紧急错误恢复 | `error` 终结符 | `error` 终结符（一致） |

### 1.2 JavaCUP 的语法规则格式

```
// JavaCUP 格式
module ::=
  MODULE identifier:id SEMICOLON
    {: RESULT = id; :}
  declarations
  BEGIN
  statement_sequence
  END identifier:id2 DOT
    {: if (!id.equals(id2)) report_error("Name mismatch", null); :}
  ;
```

关键特征：
- 使用 `{: ... :}` 标记语义动作块
- 使用 `label:name` 为符号关联标签，在语义动作中引用
- 使用 `RESULT` 设置非终结符的综合属性

### 1.3 GNU Bison 的对应语法

```
// GNU Bison 格式
module:
  MODULE identifier ';'
    { $$ = $2; }
  declarations
  BEGIN
  statement_sequence
  END identifier '.'
    { if (strcmp($2, $8) != 0) error("Name mismatch"); }
  ;
```

关键特征：
- 使用 `{ ... }` 标记语义动作块
- 使用 `$1`, `$2`, ... 引用产生式右侧符号的属性
- 使用 `$$` 设置产生式左侧符号的属性

### 1.4 对比总结

| 方面 | JavaCUP | GNU Bison |
|------|---------|-----------|
| 目标语言 | Java | C / C++ |
| 语义动作引用 | 命名标签 `:id` | 位置编号 `$1` `$2` |
| 类型声明 | 显式 `non terminal String nt;` | `%union` + `%type` |
| 可读性 | 标签名更易读 | 位置编号在长规则中易混乱 |
| 错误消息 | Java 异常机制 | C/C++ 宏/函数 |
| 平台 | JVM 跨平台 | 依赖 C 编译环境 |
| 分析算法 | LALR(1) | LALR(1) — 一致 |
| 冲突解决 | 同 yacc 默认规则 | 同 yacc 默认规则 |

---

## 2. JavaCC 与 JavaCUP 的最核心区别

**JavaCC 生成的是递归下降（自顶向下）的 LL(k) 解析器，而 JavaCUP 生成的是移进/归约（自底向上）的 LALR(1) 解析器。**

具体而言：
- **JavaCC**：将 EBNF 语法规则直接转换为递归函数调用，语义动作可嵌入在规则的任意位置（包括产生式中间），更直观地对应递归下降预测分析
- **JavaCUP**：将语法规则转换为 LALR 分析表，语义动作只能在产生式归约时执行，无法在产生式中间的任意位置插入语义动作

这意味着 JavaCC 更适合手工编写翻译模式前的原型验证，而 JavaCUP 适合处理更大类的上下文无关文法。

---

## 3. 实际使用体会

在本实验中，JavaCUP 的以下特点影响了开发体验：

1. **命名标签优于位置索引**：在复杂的 Oberon-0 语法规则中，`identifier:id` 比 `$2` 更清晰，减少了因调整产生式而导致位置偏移出错的风险
2. **类型安全性**：JavaCUP 生成 Java 代码，编译期可检查类型一致性；Bison 生成的 C 代码在处理 `%union` 时需要程序员自己保证类型安全
3. **错误报告**：JavaCUP 的冲突报告（shift/reduce, reduce/reduce）格式与 yacc 工具一致，便于调试
4. **与 JFlex 的集成**：通过 `%cup` 指令和统一的 `Symbol` 类，词法分析器的输出可直接供语法分析器使用

---

## 4. 参考文献

1. Scott E. Hudson. *CUP User's Manual*. Version 0.11b, Carnegie Mellon University.
2. GNU Bison Project. *Bison Manual*. https://www.gnu.org/software/bison/
3. JavaCC Project. *JavaCC Documentation*. https://javacc.github.io/javacc/
4. A. Aho et al. *Compilers: Principles, Techniques, and Tools*, 2nd Ed. Addison-Wesley, 2006.
