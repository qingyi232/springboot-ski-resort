# -*- coding: utf-8 -*-
import sys, io
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

from docx import Document
from docx.oxml.ns import qn

doc = Document('5.26.2.docx')
body = doc.element.body

print("=== 检查所有Heading元素的XML样式值 ===\n")

for elem in body:
    if elem.tag == qn('w:p'):
        pPr = elem.find(qn('w:pPr'))
        if pPr is not None:
            pStyle = pPr.find(qn('w:pStyle'))
            if pStyle is not None:
                style_val = pStyle.get(qn('w:val'))
                text = ''.join(r.text or '' for r in elem.findall(qn('w:r')))
                if text.strip() and ('4.4' in text or '4.5' in text or '4.6' in text or 
                    '5.3' in text or '5.4' in text or '5.5' in text or
                    '第4章' in text or '第5章' in text):
                    print(f"  style_val='{style_val}' text='{text.strip()[:60]}'")
