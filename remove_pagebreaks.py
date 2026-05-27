"""
从 5.25_v4.docx 中删除所有段前分页符(pageBreakBefore)
只删分页符，不动其他内容
"""
import docx
from docx.oxml.ns import qn
import sys

sys.stdout.reconfigure(encoding='utf-8')

path = r'F:\26毕设单\基于springboot的飞跃滑雪场管理系统的设计与实现\5.25_v4.docx'

doc = docx.Document(path)

removed = 0
for p in doc.paragraphs:
    pPr = p._element.find(qn('w:pPr'))
    if pPr is not None:
        pgBr = pPr.find(qn('w:pageBreakBefore'))
        if pgBr is not None:
            pPr.remove(pgBr)
            removed += 1
            print(f"  删除分页符: P -> {p.text[:50]}")

doc.save(path)
print(f"\n共删除 {removed} 个段前分页符")
print(f"已保存到: {path}")
