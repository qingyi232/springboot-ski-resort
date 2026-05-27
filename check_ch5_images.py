# -*- coding: utf-8 -*-
import sys, io, os
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
from docx import Document
from docx.opc.constants import RELATIONSHIP_TYPE as RT
import zipfile

doc = Document('5.26.2.docx')

in_ch5 = False
img_count = 0

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
        
        blips = para._element.findall('.//{http://schemas.openxmlformats.org/drawingml/2006/main}blip')
        if blips:
            for blip in blips:
                embed = blip.get('{http://schemas.openxmlformats.org/officeDocument/2006/relationships}embed')
                if embed:
                    img_count += 1
                    rel = doc.part.rels.get(embed)
                    if rel:
                        target = rel.target_ref
                        print(f'[Para {i}] 图片#{img_count}: rId={embed}, target={target}')
                    else:
                        print(f'[Para {i}] 图片#{img_count}: rId={embed}, rel NOT FOUND')

print(f'\n总计 {img_count} 张图片')

print('\n--- 提取第五章图片到 ch5_images 目录 ---')
os.makedirs('ch5_images', exist_ok=True)
with zipfile.ZipFile('5.26.2.docx', 'r') as z:
    for name in z.namelist():
        if name.startswith('word/media/'):
            z.extract(name, 'ch5_images')
            print(f'提取: {name}')
