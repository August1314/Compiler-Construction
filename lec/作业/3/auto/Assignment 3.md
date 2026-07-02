## Assignment 3

提交要求：

(1)截止日期:2026/5/26;

(2)可以使用电子版文档or 手写拍照，但最终应转换为pdf格式;

(3)pdf 命名格式:学号-姓名-asg3.pdf;

(4)提交链接:https://yunbiz.wps.cn/c/collect/c6OgXNPBkuR

一、在设计递归下降预测翻译器 (Recursive Descent Predictive Translator)时，会让每一个非终结符号A对应一个递归函数，其中A的每一个继承属性都对应着该函数的一个___，该函数的返回值则是A的___。(8分)

二、如果一个翻译模式 (Translation Scheme) 中存在嵌入在产生式右部的左边或中间的语义动作，该翻译模式可通过哪些3 种翻译技术实现？(12分)

三、考虑以下语法制导定义（SDD）：

<table><tr><td rowspan=1 colspan=1>语法规则</td><td rowspan=1 colspan=1>语义规则</td></tr><tr><td rowspan=1 colspan=1>T → BC</td><td rowspan=1 colspan=1>T.t = C.t、C.b = B.t</td></tr><tr><td rowspan=1 colspan=1>B → int</td><td rowspan=1 colspan=1>B.t = integer</td></tr><tr><td rowspan=1 colspan=1>B → float</td><td rowspan=1 colspan=1>B.t = float</td></tr><tr><td rowspan=1 colspan=1>C →[ num ]Ca</td><td rowspan=2 colspan=1>C.t = array(num.val, C1.t)、C1.b = C.bC.t = c.b</td></tr><tr><td rowspan=1 colspan=1>Ce</td></tr></table>

注：假设 num 对应非负整数，num.val 代表这一非负整数的值。

(1) 在这一 SDD 中，哪些属性是综合属性？哪些属性是继承属性？(9分)

(2) 对输入串 float[3][5]构造带注释的分析树。(31 分)

四、设有如下语言：

$$
{ \begin{array} { l } { E x p r  \mathbf { f o r } i d : = i n t _ { 1 } \mathbf { t o } i n t _ { 2 } \mathbf { d o } E x p r _ { 1 } } \\ { \mid i d : = E x p r _ { 1 } \phantom { { \Biggl ( } } \{ \Vert \mathbb { f } \Vert \mathbf { \ddot { f } } \Vert \mathbf { \dot { f } } \Vert \} } \\ { \mid E x p r _ { 1 } ; E x p r _ { 2 } \phantom { { \Biggl ( } } \{ \Vert \mathbb { f } \Vert \mathbf { \ddot { f } } ^ { * } \neq \mathbb { \dot { m } } \mathbf { f } \Vert \mathbf { \dot { f } } \Vert \} } \\ { \mid E x p r _ { 1 } * E x p r _ { 2 } \phantom { { \Biggl ( } } \{ \mathbf { \ddot { 3 } } \mathbb { E } \mathbf { \dot { 2 } } \mathbf { \dot { \xi } } } \} \\ { \mid E x p r _ { 1 } + E x p r _ { 2 } \phantom { { \Biggl ( } } \{ \Vert \mathbf { \dot { 3 } } \mathbf { \dot { 2 } } \} } \\ { \mid i d \ } \end{array} }
$$

定义一个表达式的开销 (cp)=其所有子表达式的开销 + 其本身的开销。

 加法的开销是 1，乘法的开销是2，顺序操作的开销是0，如 (a+E).cp= 1 + a.cp + E.cp

 赋值的开销是1，还包括作为右值的子表达式的开销。

 循环的开销是3，还包括循环中每一次迭代的子表达式开销，即子表达式.cp \* 循环次数 + 3。

 每一个变量id与常量int作为表达式的开销均为1。

(1)给出一个计算上述开销函数定义的语法制导定义 (SDD)。可假设有一个属性 val 包含单词的词法分析值；也可按自己的需要定义其他属性。(30分)

(2)请说明你在语法制导定义中引入的每一个属性是综合属性还是继承属性。(10 分)