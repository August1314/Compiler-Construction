#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"

cd "$ROOT"
xelatex -interaction=nonstopmode -halt-on-error "report.tex" >/tmp/lab2-report-xelatex-1.log
xelatex -interaction=nonstopmode -halt-on-error "report.tex" >/tmp/lab2-report-xelatex-2.log
cp "report.pdf" "Lab2 实验报告.pdf"
cp "report.pdf" "design.pdf"
