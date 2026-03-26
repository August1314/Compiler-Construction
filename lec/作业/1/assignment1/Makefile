# LaTeX compilation makefile

PDF = assignment1.pdf
TEX = assignment1.tex
BIB = references.bib

all: $(PDF)

$(PDF): $(TEX) $(BIB)
	pdflatex $(TEX)
	bibtex $(TEX:%.tex=%.aux)
	pdflatex $(TEX)
	pdflatex $(TEX)

clean:
	rm -f *.aux *.log *.out *.bbl *.blg *.pdf

view:
	open $(PDF)

.PHONY: all clean view
