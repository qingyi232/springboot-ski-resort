import sys
sys.stdout.reconfigure(encoding='utf-8')
from docx import Document
from docx.oxml.ns import qn

doc = Document('5.26.2.docx')

in_chapter5 = False
lines = []

for i, para in enumerate(doc.paragraphs):
    text = para.text.strip()
    style_name = para.style.name if para.style else ''

    if '第5章' in text:
        in_chapter5 = True
    if in_chapter5 and '第6章' in text:
        in_chapter5 = False
        break

    if in_chapter5:
        has_image = bool(para._element.findall('.//' + qn('w:drawing')))
        if text or has_image:
            marker = '[IMG]' if has_image else ''
            display = text[:180] if text else '(no text)'
            lines.append(f'P{i}: [{style_name}] {marker} {display}')

with open('ch5_verify.txt', 'w', encoding='utf-8') as f:
    f.write('\n'.join(lines))

print(f'Done, {len(lines)} lines')
