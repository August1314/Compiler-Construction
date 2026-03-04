#!/bin/bash

echo "=== 运行个人所得税计算器 ==="

# 检查bin目录是否存在
if [ ! -d "bin" ]; then
    echo "错误：bin目录不存在，请先编译程序！"
    echo "请运行 ./build.sh 编译程序"
    exit 1
fi

# 检查主类是否存在
if [ ! -f "bin/PersonalTaxCalculator.class" ]; then
    echo "错误：PersonalTaxCalculator类不存在，请先编译程序！"
    echo "请运行 ./build.sh 编译程序"
    exit 1
fi

# 运行程序
echo "启动个人所得税计算器..."
java -cp bin PersonalTaxCalculator

echo "=== 程序运行结束 ==="
