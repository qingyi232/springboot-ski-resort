# -*- coding: utf-8 -*-
import sys
sys.stdout.reconfigure(encoding='utf-8')
from docx import Document
from docx.oxml.ns import qn
from lxml import etree

doc = Document(r"F:\26毕设单\基于springboot的飞跃滑雪场管理系统的设计与实现\5.26.2.docx")

for i, p in enumerate(doc.paragraphs):
    for run in p.runs:
        drawings = run._element.findall(qn('w:drawing'))
        for d in drawings:
            # Check for inline or anchor
            inlines = d.findall(qn('wp:inline'))
            anchors = d.findall(qn('wp:anchor'))
            
            for item in inlines + anchors:
                graphic = item.find(qn('a:graphic'))
                if graphic is not None:
                    gd = graphic.find(qn('a:graphicData'))
                    if gd is not None:
                        uri = gd.get('uri', '')
                        # Check for picture
                        pic = gd.find(qn('pic:pic'))
                        if pic is not None:
                            blipfill = pic.find(qn('pic:blipFill'))
                            if blipfill is not None:
                                blip = blipfill.find(qn('a:blip'))
                                if blip is not None:
                                    embed = blip.get(qn('r:embed'), '')
                                    # Get the actual image info
                                    part = doc.part.related_parts.get(embed)
                                    if part:
                                        ct = part.content_type
                                        print(f"P{i}: embed={embed}, content_type={ct}")
                                    else:
                                        print(f"P{i}: embed={embed}, part not found")
                        
                        # Check for chart/diagram
                        chart = gd.find('{http://schemas.openxmlformats.org/drawingml/2006/chart}chart')
                        dgm = gd.find('{http://schemas.openxmlformats.org/drawingml/2006/diagram}relIds')
                        
                        if chart is not None:
                            print(f"P{i}: CHART found")
                        if dgm is not None:
                            print(f"P{i}: DIAGRAM found")
                        
                        # Print uri for debugging
                        if not pic and not chart and not dgm:
                            print(f"P{i}: unknown graphic uri={uri}")
