#!/bin/bash
set -euo pipefail

XELATEX = xelatex

TARGET_PDF = assignment4.pdf
SOURCE_TEX = assignment4.tex
OUTPUT_DIR = .

all: $(TARGET_PDF)

$(TARGET_PDF): $(SOURCE_TEX)
	$(XELATEX) -interaction=nonstopmode -output-directory=$(OUTPUT_DIR) $(SOURCE_TEX)
	$(XELATEX) -interaction=nonstopmode -output-directory=$(OUTPUT_DIR) $(SOURCE_TEX)

clean:
	rm -f *.aux *.log *.out *.synctex.gz $(TARGET_PDF)

view:
	open $(TARGET_PDF)

.PHONY: all clean view
