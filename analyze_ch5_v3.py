# -*- coding: utf-8 -*-
from docx import Document
from docx.oxml.ns import qn

doc = Document('5.26.2.docx')

ch5_start = False
ch5_items = []

for i, p in enumerate(doc.paragraphs):
    text = p.text.strip()
    style = p.style.name
    
    if '第5章' in text or '第五章' in text:
        ch5_start = True
        ch5_items.append(f'[{i}] [{style}] {text[:100]}')
        continue
    
    if ch5_start and style.startswith('Heading') and ('第6章' in text or '第六章' in text or '致谢' in text or '参考文献' in text):
        break
    
    if ch5_start and text:
        ch5_items.append(f'[{i}] [{style}] {text[:120]}')

for item in ch5_items:
    print(item)
