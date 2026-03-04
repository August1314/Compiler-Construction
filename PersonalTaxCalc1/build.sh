#!/bin/bash

echo "=== 开始编译个人所得税计算器 ==="

# 确保bin目录存在
mkdir -p bin

# 编译Java源代码
echo "编译Java源代码..."
javac -d bin src/*.java

if [ $? -eq 0 ]; then
    echo "编译成功！"
    
    # 生成Javadoc文档
    echo "生成Javadoc文档..."
    mkdir -p doc
    javadoc -d doc src/*.java
    
    if [ $? -eq 0 ]; then
        echo "Javadoc文档生成成功！"
    else
        echo "Javadoc文档生成失败！"
    fi
else
    echo "编译失败！"
    exit 1
fi

echo "=== 编译完成 ==="
