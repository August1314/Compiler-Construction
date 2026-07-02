ExprEval Lab3 实验三 自述文件
=============================

一、基本信息
------------
姓名：梁力航
学号：23336128
电子邮件：153159571+August1314@users.noreply.github.com
完成日期：2026-05-20

二、目录说明
------------
ex3/src/oberon.cup      JavaCUP 输入源文件（Oberon-0 语法规则 + 语义动作）
ex3/src/sym.java        Token 符号常量定义（由 JavaCUP 生成）
ex3/src/Parser.java     语法分析和语法制导翻译程序（LALR 风格）
ex3/bin/                编译后的字节码
ex3/doc/                由 javadoc 生成的 API 文档
ex3/build.sh            编译脚本
ex3/run.sh              运行脚本

三、实现说明
------------
1. 本实验使用 LALR 语法分析策略，通过 Oberon-0 的 EBNF 语法规则
   指导语法分析和语法制导翻译
2. 对于词法/语法/语义错误的 Oberon-0 源程序，至少指出一处错误
   并判断错误类别及位置
3. 对于正确的 Oberon-0 源程序，自动绘制模块中各过程的调用图（Call Graph）
4. 调用图集成 ROSE 软装置的 CallGraph API，使用 JGraph 进行可视化

四、错误处理
------------
词法错误：IllegalSymbolException, IllegalIntegerException,
         IllegalOctalException, IllegalIdentifierLengthException,
         MismatchedCommentException, IllegalIntegerRangeException
语法错误：MissingLeftParenthesisException,
         MissingRightParenthesisException,
         MissingOperatorException, MissingOperandException
语义错误：TypeMismatchedException, ParameterMismatchedException
