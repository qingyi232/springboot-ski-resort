# -*- coding: utf-8 -*-
import sys, io
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
from docx import Document

def analyze_ch5_detail(filename):
    print(f'\n=== {filename} ===\n')
    doc = Document(filename)
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
            blips = para._element.findall('.//{http://schemas.openxmlformats.org/drawingml/2006/main}blip')
            img_info = ""
            if blips:
                has_image = True
                embed = blips[0].get('{http://schemas.openxmlformats.org/officeDocument/2006/relationships}embed')
                rel = doc.part.rels.get(embed) if embed else None
                img_info = f" [IMG->{rel.target_ref}]" if rel else " [IMG]"
            
            if 'Heading' in style_name or text or has_image:
                display = text[:90] if text else "(图片)"
                print(f'[{i}]{img_info} [{style_name}] {display}')

analyze_ch5_detail('5.26.2_backup.docx')
