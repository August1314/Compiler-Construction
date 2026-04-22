#\!/bin/bash
set -euo pipefail

XELATEX = xelatex
BIBTEX = bibtex

TARGET_PDF = assignment2.pdf
SOURCE_TEX = assignment2.tex
REFERENCES_BIB = references.bib
OUTPUT_DIR = .
INTERMEDIATE_DIR = .

# Default target
all: $(TARGET_PDF)

$(TARGET_PDF): $(SOURCE_TEX)
	$(XELATEX) -interaction=nonstopmode -output-directory=$(OUTPUT_DIR) $(SOURCE_TEX)
	if [ -s $(INTERMEDIATE_DIR)/$(REFERENCES_BIB) ]; then \
		$(BIBTEX_CMD) $(OUTPUT_DIR)/$(SOURCE_TEX:.tex=)
		$(XELATEX) -interaction=nonstopmode -output-directory=$(OUTPUT_DIR) $(SOURCE_TEX)
	fi
	$(XELATEX) -interaction=nonstopmode -output-directory=$(OUTPUT_DIR) $(SOURCE_TEX)

clean:
	rm -f $(INTERMEDIATE_DIR)/*.aux $(INTERMEDIATE_DIR)/*.log $(INTERMEDIATE_DIR)/*.out $(INTERMEDIATE_DIR)/*.bbl $(INTERMEDIATE_DIR)/*.blg $(INTERMEDIATE_DIR)/$(TARGET_PDF)
	rm -f $(INTERMEDIATE_DIR)/*.toc $(INTERMEDIATE_DIR)/*.nav $(INTERMEDIATE_DIR)/*.snm
	rm -f $(INTERMEDIATE_DIR)/*.lof $(INTERMEDIATE_DIR)/*.lot
	rm -f $(INTERMEDIATE_DIR)/*.out.aux $(INTERMEDIATE_DIR)/*.out.log
	rm -f $(TARGET_PDF)

view:
	open $(TARGET_PDF)

.PHONY: all clean view
