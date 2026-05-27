import sys
sys.stdout.reconfigure(encoding='utf-8')
from docx import Document
from docx.oxml.ns import qn
import re

doc = Document('5.26.2.docx')

updates = [
    (187, '图5.2', '图5.1'),
    (267, '图5.3', '图5.4'),
    (294, '图 5.4', '图5.5'),
    (322, '图5.5', '图5.6'),
]

for pi, old_text, new_text in updates:
    para = doc.paragraphs[pi]
    full_text = para.text.strip()
    print(f"\nP{pi}: '{full_text[:60]}'")

    if old_text not in full_text and old_text.replace(' ', '') not in full_text.replace(' ', ''):
        print(f"  WARNING: '{old_text}' not found in text!")
        continue

    runs = para.runs
    combined = ''
    for r in runs:
        combined += r.text

    old_digit = old_text[-1]
    new_digit = new_text[-1]

    found = False
    for i, run in enumerate(runs):
        if run.text and '5.' in run.text:
            if old_digit in run.text and run.text.index(old_digit) > run.text.index('5.'):
                run.text = run.text.replace(f'5.{old_digit}', f'5.{new_digit}')
                found = True
                print(f"  Fixed in run[{i}]: '5.{old_digit}' -> '5.{new_digit}'")
                break

    if not found:
        for i, run in enumerate(runs):
            if run.text and '5.' in run.text:
                if i + 1 < len(runs) and runs[i + 1].text and runs[i + 1].text.startswith(old_digit):
                    runs[i + 1].text = new_digit + runs[i + 1].text[1:]
                    found = True
                    print(f"  Fixed digit in run[{i+1}]: '{old_digit}...' -> '{new_digit}...'")
                    break

    if not found:
        for i, run in enumerate(runs):
            if run.text == old_digit:
                prev_has_dot = i > 0 and runs[i-1].text and runs[i-1].text.endswith('5.')
                if prev_has_dot:
                    run.text = new_digit
                    found = True
                    print(f"  Fixed standalone digit run[{i}]: '{old_digit}' -> '{new_digit}'")
                    break

    if not found:
        print(f"  ERROR: Could not fix figure number!")
        for i, run in enumerate(runs):
            print(f"    run[{i}]: '{run.text}'")

    if pi == 294:
        for i, run in enumerate(runs):
            if run.text == ' ' and i > 0 and runs[i-1].text == '图':
                run.text = ''
                print(f"  Removed space in run[{i}]")
            if '  ' in (run.text or ''):
                run.text = run.text.replace('  ', '　')
                print(f"  Normalized spacing in run[{i}]")

    print(f"  Result: '{para.text.strip()[:60]}'")

doc.save('5.26.2.docx')
print("\nDone! Figure numbers updated")
