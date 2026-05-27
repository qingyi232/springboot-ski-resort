# -*- coding: utf-8 -*-
import sys, io
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

from docx import Document
from docx.oxml.ns import qn

doc = Document('5.26.2.docx')

print("=== 目录段落（前70段） ===\n")
for i, p in enumerate(doc.paragraphs[:70]):
    text = p.text.strip()
    style = p.style.name
    if text:
        print(f"[{i}] [{style}] {text[:80]}")
