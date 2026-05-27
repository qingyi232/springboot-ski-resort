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

    if i == 178:
        in_chapter5 = True
    if i == 370:
        break

    if in_chapter5:
        has_image = False
        drawings = para._element.findall('.//' + qn('w:drawing'))
        picts = para._element.findall('.//' + qn('w:pict'))
        if drawings or picts:
            has_image = True

        if text or has_image:
            marker = '[IMG]' if has_image else ''
            display_text = text[:200] if text else '(no text)'
            lines.append(f'P{i}: [{style_name}] {marker} {display_text}')

with open('ch5_analysis.txt', 'w', encoding='utf-8') as f:
    f.write('\n'.join(lines))

print(f'Done, {len(lines)} lines written')
