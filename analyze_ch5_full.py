import sys
sys.stdout.reconfigure(encoding='utf-8')
from docx import Document
from docx.oxml.ns import qn
from lxml import etree

doc = Document('5.26.2.docx')
body = doc.element.body

lines = []
para_idx = 0
table_idx = 0
in_chapter5 = False
element_list = []

for child in body:
    tag = child.tag.split('}')[-1] if '}' in child.tag else child.tag
    
    if tag == 'p':
        text = child.text or ''
        for t_elem in child.findall('.//' + qn('w:t')):
            if t_elem.text:
                text += t_elem.text
        text = text.strip()
        
        style_elem = child.find(qn('w:pPr'))
        style_name = ''
        if style_elem is not None:
            ps = style_elem.find(qn('w:pStyle'))
            if ps is not None:
                style_name = ps.get(qn('w:val'), '')
        
        if para_idx == 178:
            in_chapter5 = True
        if para_idx == 370:
            in_chapter5 = False
        
        if in_chapter5:
            has_image = bool(child.findall('.//' + qn('w:drawing'))) or bool(child.findall('.//' + qn('w:pict')))
            marker = '[IMG]' if has_image else ''
            display = text[:200] if text else '(no text)'
            lines.append(f'ELEM[{len(element_list)}] P{para_idx}: [{style_name}] {marker} {display}')
            element_list.append(('para', para_idx))
        
        para_idx += 1
    
    elif tag == 'tbl':
        if in_chapter5:
            rows = child.findall(qn('w:tr'))
            first_row_text = ''
            if rows:
                for cell in rows[0].findall('.//' + qn('w:t')):
                    if cell.text:
                        first_row_text += cell.text + ' '
            lines.append(f'ELEM[{len(element_list)}] TABLE-{table_idx}: [{len(rows)} rows] First row: {first_row_text[:100]}')
            element_list.append(('table', table_idx))
        table_idx += 1

with open('ch5_full_analysis.txt', 'w', encoding='utf-8') as f:
    f.write('\n'.join(lines))

print(f'Done, {len(lines)} elements')
