"""
恢复章节标题的段前分页符（章节应该从新页开始）
只恢复 Heading 1 标题和特殊章节，不恢复表格标题
"""
import docx
from docx.oxml.ns import qn, nsdecls
from docx.oxml import parse_xml
import sys

sys.stdout.reconfigure(encoding='utf-8')

path = r'F:\26毕设单\基于springboot的飞跃滑雪场管理系统的设计与实现\5.25_v4.docx'
doc = docx.Document(path)

restored = 0
for p in doc.paragraphs:
    text = p.text.strip()
    style = p.style.name if p.style else ''
    
    need_break = False
    if style == 'Heading 1':
        need_break = True
    elif text == 'ABSTRACT':
        need_break = True
    elif text.startswith('参考文献'):
        need_break = True
    elif text.startswith('致'):
        need_break = True
    
    if need_break:
        pPr = p._element.find(qn('w:pPr'))
        if pPr is None:
            pPr = parse_xml(f'<w:pPr {nsdecls("w")}></w:pPr>')
            p._element.insert(0, pPr)
        existing = pPr.find(qn('w:pageBreakBefore'))
        if existing is None:
            pPr.append(parse_xml(f'<w:pageBreakBefore {nsdecls("w")}/>'))
            restored += 1
            print(f"  恢复分页: [{style}] {text[:40]}")

doc.save(path)
print(f"\n恢复了 {restored} 个章节分页符")
