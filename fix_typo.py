# -*- coding: utf-8 -*-
import docx
doc = docx.Document(r'F:\26毕设单\基于springboot的飞跃滑雪场管理系统的设计与实现\5.25.docx')
for p in doc.paragraphs:
    for r in p.runs:
        if '\u7ed1\u5236' in r.text:
            r.text = r.text.replace('\u7ed1\u5236', '\u7ed8\u5236')
            print('fixed typo')
doc.save(r'F:\26毕设单\基于springboot的飞跃滑雪场管理系统的设计与实现\5.25.docx')
print('saved')
