# -*- coding: utf-8 -*-
from docx import Document
from docx.oxml.ns import qn
from lxml import etree

doc = Document('5.26.2.docx')

ch5_start = False
print("=== 第五章完整结构（含图片位置） ===\n")

for i, p in enumerate(doc.paragraphs):
    text = p.text.strip()
    style = p.style.name
    
    if '第5章' in text or '第五章' in text:
        ch5_start = True
    
    if ch5_start and style.startswith('Heading') and ('第6章' in text or '第六章' in text or '致谢' in text or '参考文献' in text):
        break
    
    if not ch5_start:
        continue
    
    has_image = False
    img_count = 0
    for run in p.runs:
        drawings = run._element.findall(qn('w:drawing'))
        for d in drawings:
            img_count += 1
            has_image = True
    
    inline_shapes = p._element.findall('.//' + qn('w:drawing'))
    if inline_shapes:
        has_image = True
        img_count = len(inline_shapes)
    
    if style.startswith('Heading'):
        print(f'\n[{i}] <<{style}>> {text}')
    elif has_image:
        print(f'[{i}] [IMG x{img_count}] {text[:80]}')
    elif '图' in text and ('5.' in text or '5-' in text or '5．' in text):
        print(f'[{i}] [图题] {text}')
    elif '核心代码' in text:
        print(f'[{i}] [代码标记] {text}')
    elif text and (text.startswith('@') or text.startswith('public') or text.startswith('private') or text.startswith('router') or text.startswith('const') or text.startswith('if ') or text.startswith('return') or text.startswith('next(') or text.startswith('}') or text.startswith('{') or text.startswith('.') or text.startswith('Long') or text.startswith('Map') or text.startswith('List') or text.startswith('Date') or text.startswith('Integer') or text.startswith('String') or text.startswith('PageResult') or text.startswith('Coach') or text.startswith('Product') or text.startswith('RentalOrder') or text.startswith('overview') or text.startswith('result') or text.startswith('order') or text.startswith('product') or text.startswith('new ') or text.startswith('long ') or text.startswith('claims')):
        print(f'[{i}] [CODE] {text[:60]}')
    elif text:
        short = text[:80]
        print(f'[{i}] [文本] {short}')
