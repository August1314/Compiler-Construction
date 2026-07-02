ExprEval Lab3 实验二 自述文件
=============================

一、基本信息
------------
姓名：梁力航
学号：23336128
电子邮件：153159571+August1314@users.noreply.github.com
完成日期：2026-05-20

二、目录说明
------------
ex2/src/exceptions/     预定义异常类层次（OberonException → Lexical/Syntactic/Semantic）
ex2/src/oberon.flex     JFlex 输入源文件（词法规则的正则定义式）
ex2/src/OberonScanner.java  手写的 Oberon-0 词法分析程序
ex2/bin/                编译后的字节码
ex2/doc/                由 javadoc 生成的 API 文档
ex2/build.sh            编译脚本
ex2/run.sh              运行脚本
ex2/test.sh             测试脚本

三、词汇表概要
--------------
保留字（19个）：MODULE, BEGIN, END, CONST, TYPE, VAR, PROCEDURE,
               IF, THEN, ELSIF, ELSE, WHILE, DO, ARRAY, OF, RECORD,
               DIV, MOD, OR
关键字（7个）：INTEGER, BOOLEAN, TRUE, FALSE, READ, WRITE, WRITELN
运算符（9个）：:=  =  #  <  <=  >  >=  +  -  *  &  ~
分隔符（8个）：;  .  ,  :  (  )  [  ]
注释定界符：(*  *)
