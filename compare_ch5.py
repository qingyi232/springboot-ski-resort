# -*- coding: utf-8 -*-
from docx import Document
from docx.oxml.ns import qn

def analyze_ch5(filename):
    print(f"\n{'='*60}")
    print(f"文件: {filename}")
    print(f"{'='*60}")
    
    doc = Document(filename)
    rels = doc.part.rels
    
    ch5_start = False
    
    for i, p in enumerate(doc.paragraphs):
        text = p.text.strip()
        style = p.style.name
        
        if '第5章' in text or '第五章' in text:
            ch5_start = True
        
        if ch5_start and style.startswith('Heading') and ('第6章' in text or '第六章' in text or '致谢' in text or '参考文献' in text):
            break
        
        if not ch5_start:
            continue
        
        drawings = p._element.findall('.//' + qn('w:drawing'))
        has_img = len(drawings) > 0
        img_info = ""
        if has_img:
            for d in drawings:
                blips = d.findall('.//' + qn('a:blip'))
                for blip in blips:
                    embed = blip.get(qn('r:embed'))
                    if embed and embed in rels:
                        img_info = f" -> {rels[embed].target_ref}"
        
        if style.startswith('Heading'):
            print(f'\n[{i}] <<{style}>> {text}')
        elif has_img:
            print(f'[{i}] [IMG{img_info}] {text[:50]}')
        elif '图5' in text or '图 5' in text:
            print(f'[{i}] [图题] {text}')
        elif '核心代码' in text:
            print(f'[{i}] [代码标记] {text[:60]}')
        elif text.startswith('@') or text.startswith('public ') or text.startswith('private ') or text.startswith('router.') or text.startswith('const '):
            print(f'[{i}] [CODE_START] {text[:50]}')

analyze_ch5('5.26.1.docx')
