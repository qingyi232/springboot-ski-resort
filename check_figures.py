import sys
sys.stdout.reconfigure(encoding='utf-8')
from docx import Document
from docx.oxml.ns import qn

doc = Document('5.26.2.docx')

target_pis = [205, 232, 242, 267, 294, 322]

for pi in target_pis:
    para = doc.paragraphs[pi]
    print(f"\nP{pi}: style={para.style.name}")
    print(f"  .text = '{para.text}'")
    for ri, run in enumerate(para.runs):
        print(f"  run[{ri}]: '{run.text}'")
    for ti, t_elem in enumerate(para._element.findall('.//' + qn('w:t'))):
        print(f"  w:t[{ti}]: '{t_elem.text}'")
