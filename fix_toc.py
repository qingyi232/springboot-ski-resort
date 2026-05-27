# -*- coding: utf-8 -*-
import sys, io
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

from docx import Document
from docx.oxml.ns import qn

doc = Document('5.26.2.docx')
body = doc.element.body

sdts = body.findall(qn('w:sdt'))
sdt = sdts[0]
content = sdt.find(qn('w:sdtContent'))
paras = content.findall(qn('w:p'))

print("=== 当前目录内容 ===")
for j, p in enumerate(paras):
    text = ''.join(r.text or '' for r in p.findall('.//' + qn('w:r')))
    if text.strip():
        print(f"  [{j}] {text.strip()[:80]}")

# 需要修改的条目
toc_changes = {
    '业务流程设计': ('4.4', '数据库设计'),
    '数据库设计': ('4.5', '业务流程设计'),
    '4.5.1': ('4.4.1', None),
    '4.5.2': ('4.4.2', None),
    '教练模块的实现': ('5.3', '工作人员模块的实现'),
    '工作人员模块的实现': ('5.4', '教练模块的实现'),
}

print("\n=== 修改目录 ===")
changes = 0

# Strategy: find each run in TOC paragraphs and update text
for j, p in enumerate(paras):
    full_text = ''.join(r.text or '' for r in p.findall('.//' + qn('w:r')))
    
    if '4.4' in full_text and '业务流程' in full_text:
        # Change to 数据库设计
        for r in p.findall('.//' + qn('w:r')):
            t = r.find(qn('w:t'))
            if t is not None and t.text:
                if '业务流程设计' in t.text:
                    t.text = t.text.replace('业务流程设计', '数据库设计')
                    changes += 1
                    print(f"  [{j}] 4.4 业务流程设计 -> 数据库设计")
    
    elif '4.5' in full_text and '数据库设计' in full_text and '4.5.1' not in full_text and '4.5.2' not in full_text:
        for r in p.findall('.//' + qn('w:r')):
            t = r.find(qn('w:t'))
            if t is not None and t.text:
                if '数据库设计' in t.text:
                    t.text = t.text.replace('数据库设计', '业务流程设计')
                    changes += 1
                    print(f"  [{j}] 4.5 数据库设计 -> 业务流程设计")
    
    elif '4.5.1' in full_text:
        for r in p.findall('.//' + qn('w:r')):
            t = r.find(qn('w:t'))
            if t is not None and t.text:
                if '4.5.1' in t.text:
                    t.text = t.text.replace('4.5.1', '4.4.1')
                    changes += 1
                    print(f"  [{j}] 4.5.1 -> 4.4.1")
    
    elif '4.5.2' in full_text:
        for r in p.findall('.//' + qn('w:r')):
            t = r.find(qn('w:t'))
            if t is not None and t.text:
                if '4.5.2' in t.text:
                    t.text = t.text.replace('4.5.2', '4.4.2')
                    changes += 1
                    print(f"  [{j}] 4.5.2 -> 4.4.2")
    
    elif '5.3' in full_text and '教练' in full_text:
        for r in p.findall('.//' + qn('w:r')):
            t = r.find(qn('w:t'))
            if t is not None and t.text:
                if '教练模块' in t.text:
                    t.text = t.text.replace('教练模块', '工作人员模块')
                    changes += 1
                    print(f"  [{j}] 5.3 教练模块 -> 工作人员模块")
    
    elif '5.4' in full_text and '工作人员' in full_text:
        for r in p.findall('.//' + qn('w:r')):
            t = r.find(qn('w:t'))
            if t is not None and t.text:
                if '工作人员模块' in t.text:
                    t.text = t.text.replace('工作人员模块', '教练模块')
                    changes += 1
                    print(f"  [{j}] 5.4 工作人员模块 -> 教练模块")

doc.save('5.26.2.docx')
print(f"\n共修改 {changes} 处目录条目, 已保存")

# 验证
print("\n=== 验证修改后目录 ===")
doc2 = Document('5.26.2.docx')
body2 = doc2.element.body
sdts2 = body2.findall(qn('w:sdt'))
content2 = sdts2[0].find(qn('w:sdtContent'))
paras2 = content2.findall(qn('w:p'))

for j, p in enumerate(paras2):
    text = ''.join(r.text or '' for r in p.findall('.//' + qn('w:r')))
    if text.strip() and ('4.4' in text or '4.5' in text or '5.3' in text or '5.4' in text):
        print(f"  [{j}] {text.strip()[:80]}")
