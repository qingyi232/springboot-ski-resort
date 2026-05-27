import sys
sys.stdout.reconfigure(encoding='utf-8')
from docx import Document
from docx.oxml.ns import qn
from lxml import etree
import shutil

shutil.copy2('5.26.2.docx', '5.26.2_backup.docx')
print("Backup created: 5.26.2_backup.docx")

doc = Document('5.26.2.docx')
body = doc.element.body

children_snapshot = list(body)
para_idx = 0
pi_to_bi = {}

for bi, child in enumerate(children_snapshot):
    tag = etree.QName(child.tag).localname
    if tag == 'p':
        pi_to_bi[para_idx] = bi
        para_idx += 1

table_bi = None
for bi in range(pi_to_bi[181] + 1, pi_to_bi[182]):
    if etree.QName(children_snapshot[bi].tag).localname == 'tbl':
        table_bi = bi
        break

if table_bi is None:
    print("ERROR: Table not found between P181 and P182")
    sys.exit(1)

print(f"Table found at body index {table_bi}")

range_start = pi_to_bi[179]
range_end = pi_to_bi[238]

elements_in_range = {}
for bi in range(range_start, range_end):
    elements_in_range[bi] = children_snapshot[bi]

print(f"Section 5.1 range: body[{range_start}:{range_end}], {len(elements_in_range)} elements")

desired_order = []

desired_order.extend([
    pi_to_bi[179],
    pi_to_bi[180],
    pi_to_bi[181],
    table_bi,
])

desired_order.extend([
    pi_to_bi[236],
    pi_to_bi[235],
    pi_to_bi[210],
    pi_to_bi[234],
    pi_to_bi[233],
    pi_to_bi[232],
    pi_to_bi[231],
])

for pi in range(230, 210, -1):
    desired_order.append(pi_to_bi[pi])

desired_order.extend([
    pi_to_bi[237],
    pi_to_bi[209],
    pi_to_bi[208],
    pi_to_bi[207],
    pi_to_bi[206],
    pi_to_bi[205],
    pi_to_bi[182],
])

for pi in range(204, 182, -1):
    desired_order.append(pi_to_bi[pi])

desired_set = set(desired_order)
actual_set = set(elements_in_range.keys())

if desired_set != actual_set:
    missing = actual_set - desired_set
    extra = desired_set - actual_set
    print(f"WARNING: Missing from desired: {missing}")
    print(f"WARNING: Extra in desired: {extra}")
    for bi in sorted(missing):
        tag = etree.QName(children_snapshot[bi].tag).localname
        print(f"  bi={bi}, tag={tag}")
    sys.exit(1)

if len(desired_order) != len(set(desired_order)):
    print("ERROR: Duplicate body indices in desired order!")
    sys.exit(1)

print(f"Desired order has {len(desired_order)} elements, all verified")

anchor = children_snapshot[range_start - 1]

for bi in sorted(elements_in_range.keys(), reverse=True):
    body.remove(elements_in_range[bi])

anchor_new_idx = list(body).index(anchor)
for i, bi in enumerate(desired_order):
    body.insert(anchor_new_idx + 1 + i, elements_in_range[bi])

print("Section 5.1 reordered successfully")

figure_updates = {
    232: ("5.2", "5.1"),
    205: ("5.6", "5.2"),
    242: ("5.1", "5.3"),
    267: ("5.3", "5.4"),
}

for pi, (old_num, new_num) in figure_updates.items():
    elem = children_snapshot[pi_to_bi[pi]]
    for t_elem in elem.findall('.//' + qn('w:t')):
        if t_elem.text and f'5.{old_num.split(".")[1]}' in t_elem.text:
            old_pattern = f'图{old_num}'
            new_pattern = f'图{new_num}'
            if old_pattern in t_elem.text:
                t_elem.text = t_elem.text.replace(old_pattern, new_pattern)
                print(f"  P{pi}: '{old_pattern}' -> '{new_pattern}'")
            old_pattern2 = f'图 {old_num}'
            if old_pattern2 in t_elem.text:
                t_elem.text = t_elem.text.replace(old_pattern2, new_pattern)
                print(f"  P{pi}: '{old_pattern2}' -> '{new_pattern}'")

p294_elem = children_snapshot[pi_to_bi[294]]
for t_elem in p294_elem.findall('.//' + qn('w:t')):
    if t_elem.text:
        if '图 5.4' in t_elem.text:
            t_elem.text = t_elem.text.replace('图 5.4', '图5.5')
            print(f"  P294: '图 5.4' -> '图5.5'")
        elif '图5.4' in t_elem.text:
            t_elem.text = t_elem.text.replace('图5.4', '图5.5')
            print(f"  P294: '图5.4' -> '图5.5'")

p322_elem = children_snapshot[pi_to_bi[322]]
for t_elem in p322_elem.findall('.//' + qn('w:t')):
    if t_elem.text and '图5.5' in t_elem.text:
        t_elem.text = t_elem.text.replace('图5.5', '图5.6')
        print(f"  P322: '图5.5' -> '图5.6'")

doc.save('5.26.2.docx')
print("\nDone! 5.26.2.docx saved successfully")
