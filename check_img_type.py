# -*- coding: utf-8 -*-
import sys, io
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
from docx import Document
from lxml import etree

doc = Document('5.26.2.docx')
in_ch5 = False

for i, para in enumerate(doc.paragraphs):
    text = para.text.strip()
    style_name = para.style.name if para.style else ''
    
    if not in_ch5:
        if ('第五章' in text or '第5章' in text) and 'Heading' in style_name:
            in_ch5 = True
    
    if in_ch5:
        if ('第六章' in text or '第6章' in text or '结论' in text) and 'Heading 1' in style_name:
            break
        
        wp_ns = 'http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing'
        
        inlines = para._element.findall(f'.//{{{wp_ns}}}inline')
        anchors = para._element.findall(f'.//{{{wp_ns}}}anchor')
        
        if inlines or anchors:
            print(f'[Para {i}] inline={len(inlines)}, anchor(浮动)={len(anchors)}, text="{text[:50]}"')
            
            if anchors:
                for a in anchors:
                    print(f'  -> 浮动图片! wrap type:', end=' ')
                    wrap_types = ['wrapNone', 'wrapSquare', 'wrapTight', 'wrapThrough', 'wrapTopAndBottom']
                    for wt in wrap_types:
                        if a.findall(f'{{{wp_ns}}}{wt}'):
                            print(wt, end=' ')
                    print()
