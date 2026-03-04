个人所得税计算器 (PersonalTaxCalc1)
=====================================

一、基本信息
------------
姓名：[梁力航]
学号：[23336128]
日期：2026 年 3 月 4日

二、文件说明
------------
本项目文件夹包含以下内容：

1. src/
   └── PersonalTaxCalculator.java    - 主程序源代码文件
                                      包含 PersonalTaxCalculator 主类和 TaxRate 辅助类

2. bin/
   └── *.class                       - 编译后的 Java 字节码文件（运行 build.sh 后生成）

3. doc/
   └── *.html                        - Javadoc 自动生成的 API 文档

4. build.sh                          - Linux/Mac 编译脚本
5. build.bat                         - Windows 编译脚本
6. run.sh                            - Linux/Mac 运行脚本
7. run.bat                           - Windows 运行脚本

8. testcase-*.bat                    - 回归测试脚本
   ├── testcase-001.bat              - 测试用例 1：正常工资收入纳税计算
   ├── testcase-002.bat              - 测试用例 2：低于起征点收入
   ├── testcase-003.bat              - 测试用例 3：高收入纳税计算
   └── testcase-004.bat              - 测试用例 4：边界值测试

9. design.doc                        - 面向对象程序设计文档
10. README.md                        - 项目说明文档（Markdown 格式）
11. readme.txt                       - 本自述文件

三、功能概述
------------
本程序是一个基于命令行界面的个人所得税计算器，主要功能包括：
- 根据用户输入的当月工资薪金总额计算应缴纳的个人所得税额
- 支持调整个人所得税起征点
- 支持修改个人所得税各级税率
- 提供命令行菜单选择功能

四、编译与运行
--------------
Linux/Mac 系统：
  ./build.sh    # 编译程序
  ./run.sh      # 运行程序

Windows 系统：
  build.bat     # 编译程序
  run.bat       # 运行程序

五、测试
--------
Windows 系统可运行测试脚本进行回归测试：
  testcase-001.bat
  testcase-002.bat
  testcase-003.bat
  testcase-004.bat

六、编码规范
------------
- 遵循 Sun 公司（现为 Oracle 公司）Java 编码规范
- 使用文档化注释，支持 Javadoc 自动生成
- 采用面向对象风格设计
