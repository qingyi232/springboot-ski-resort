# -*- coding: utf-8 -*-
import docx

doc = docx.Document('5.26.2.docx')

in_ch5 = False
ch5_elements = []

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
            if run._element.findall('.//{http://schemas.openxmlformats.org/wordprocessingml/2006/main}drawing'):
                has_image = True
                break
            if run._element.findall('.//{http://schemas.openxmlformats.org/wordprocessingml/2006/main}pict'):
                has_image = True
                break
        
        inline_shapes = []
        for rel in para._element.findall('.//{http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing}inline'):
            inline_shapes.append(rel)
        
        img_marker = " [HAS_IMAGE]" if (has_image or inline_shapes) else ""
        
        if text or has_image or inline_shapes:
            ch5_elements.append((i, style_name, text[:120] + img_marker))

for idx, style, text in ch5_elements:
    print(f'[{idx}] [{style}] {text}')
