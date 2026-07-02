#!/usr/bin/env bash
# Build all Lab3 LaTeX reports and generate PDFs.
# Requires: xelatex (MacTeX or BasicTeX)
set -euo pipefail

LAB3="/Users/lianglihang/Downloads/Compiler-Construction/lab/Lab_3_实验软装置（Oberon-0逆向工程工具ROSE）"

build_tex() {
  local dir="$1"
  local texfile="$2"
  echo "=== Building $dir/$texfile ==="
  cd "$LAB3/$dir"
  xelatex -interaction=nonstopmode "$texfile" > /dev/null 2>&1
  xelatex -interaction=nonstopmode "$texfile" > /dev/null 2>&1  # second pass for TOC
  echo "  → $(basename "$texfile" .tex).pdf generated"
}

# Build each report
build_tex "ex1" "Oberon-0.tex"
build_tex "ex2" "lexgen.tex"
build_tex "ex3" "yaccgen.tex"
build_tex "ex4" "scheme.tex"

echo ""
echo "All PDFs generated successfully."
echo ""
echo "Output files:"
echo "  ex1/Oberon-0.pdf"
echo "  ex2/lexgen.pdf"
echo "  ex3/yaccgen.pdf"
echo "  ex4/scheme.pdf"
