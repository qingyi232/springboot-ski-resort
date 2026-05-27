# -*- coding: utf-8 -*-
import sys
import io
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

import docx

doc = docx.Document('5.26.2.docx')

in_ch5 = False

for i, para in enumerate(doc.paragraphs):
    text = para.text.strip()
    style_name = para.style.name if para.style else ''
    
    if not in_ch5:
        if ('第五章' in text or '第5章' in text) and 'Heading' in style_name:
            in_ch5 = True
        elif '系统实现' in text and 'Heading 1' in style_name:
            in_ch5 = True
    
    if in_ch5:
        if ('第六章' in text or '第6章' in text or '结论' in text) and 'Heading 1' in style_name:
            break
        
        has_image = False
        for run in para.runs:
            drawing = run._element.findall('.//{http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing}inline')
            if drawing:
                has_image = True
                break
            drawing2 = run._element.findall('.//{http://schemas.openxmlformats.org/wordprocessingml/2006/main}drawing')
            if drawing2:
                has_image = True
                break
        
        if not has_image:
            ns = '{http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing}'
            for child in para._element:
                if child.findall(f'.//{ns}inline'):
                    has_image = True
                    break
        
        img_tag = " [IMG]" if has_image else ""
        
        if text or has_image:
            display = text[:100] if text else "(empty)"
            print(f'[{i}] [{style_name}]{img_tag} {display}')
