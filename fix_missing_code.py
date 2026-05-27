"""
在 5.25_v4.docx 中：
1. 找到没有核心代码的截图，在后面添加"核心代码："+ 代码
2. "核心代码："使用正文格式
只修改缺少代码的部分，不动其他内容
"""
import docx
from docx.oxml.ns import qn, nsdecls
from docx.oxml import parse_xml
from copy import deepcopy
import sys

sys.stdout.reconfigure(encoding='utf-8')

src = r'F:\26毕设单\基于springboot的飞跃滑雪场管理系统的设计与实现\5.25_v4.docx'
dst = r'F:\26毕设单\基于springboot的飞跃滑雪场管理系统的设计与实现\5.25_v4.docx'

doc = docx.Document(src)
paragraphs = doc.paragraphs

def escape_xml(text):
    return text.replace('&', '&amp;').replace('<', '&lt;').replace('>', '&gt;').replace('"', '&quot;')

# 获取正文行距
def get_body_spacing():
    for p in paragraphs:
        if p.style and p.style.name == 'Normal' and len(p.text) > 50:
            pPr = p._element.find(qn('w:pPr'))
            if pPr is not None:
                sp = pPr.find(qn('w:spacing'))
                if sp is not None:
                    return deepcopy(sp)
    return None

body_spacing = get_body_spacing()

# 找一个正文段落作为格式参考
body_ref = None
for p in paragraphs:
    if p.style and p.style.name == 'Normal' and len(p.text) > 50 and not any(kw in p.text for kw in ['@', 'public ', 'return ', 'router.', 'const ']):
        body_ref = p
        break

print(f"正文参考段落: {body_ref.text[:40] if body_ref else 'None'}")

def create_paragraph(text, use_body_format=False):
    """创建段落"""
    ref = body_ref if use_body_format else body_ref
    new_p = deepcopy(ref._element)
    # 清除所有run
    for r in new_p.findall(qn('w:r')):
        new_p.remove(r)
    
    # 确保有pPr
    pPr = new_p.find(qn('w:pPr'))
    if pPr is None:
        pPr = parse_xml(f'<w:pPr {nsdecls("w")}></w:pPr>')
        new_p.insert(0, pPr)
    
    # 设置行距
    old_sp = pPr.find(qn('w:spacing'))
    if old_sp is not None:
        pPr.remove(old_sp)
    if body_spacing is not None:
        pPr.append(deepcopy(body_spacing))
    
    # 获取参考run格式
    ref_runs = ref._element.findall(qn('w:r'))
    rPr_copy = None
    if ref_runs:
        rPr = ref_runs[0].find(qn('w:rPr'))
        if rPr is not None:
            rPr_copy = deepcopy(rPr)
    
    new_r = parse_xml(f'<w:r {nsdecls("w")}><w:t xml:space="preserve">{escape_xml(text)}</w:t></w:r>')
    if rPr_copy is not None:
        new_r.insert(0, rPr_copy)
    new_p.append(new_r)
    return new_p

def insert_code_block_after(paragraph, code_lines):
    """在指定段落后插入 '核心代码：' + 代码行"""
    current = paragraph._element
    
    # 先插入"核心代码："
    label_p = create_paragraph("核心代码：", use_body_format=True)
    current.addnext(label_p)
    current = label_p
    
    # 插入代码行
    for line in code_lines:
        code_p = create_paragraph(line, use_body_format=True)
        current.addnext(code_p)
        current = code_p

# ──────────────────────────────────────────
# 扫描第5章，找出截图后面没有代码的位置
# ──────────────────────────────────────────
in_chapter5 = False
missing_code_after = []

for i, p in enumerate(paragraphs):
    text = p.text.strip()
    
    if '第5章' in text:
        in_chapter5 = True
    elif '第6章' in text:
        in_chapter5 = False
    
    if not in_chapter5:
        continue
    
    # 找图片标题（如 "图5.5 工作人员模块的实现"）
    if text.startswith('图') and ('5.' in text or '5．' in text) and len(text) < 30:
        # 检查下面的段落是否有代码
        has_code_after = False
        for j in range(i+1, min(i+5, len(paragraphs))):
            next_text = paragraphs[j].text.strip()
            if '核心代码' in next_text or '@' in next_text[:5] or 'public ' in next_text or 'router.' in next_text:
                has_code_after = True
                break
            if next_text.startswith('5.') or next_text.startswith('第'):
                break
        
        if not has_code_after:
            missing_code_after.append((i, text))
            print(f"  缺少代码: P{i} -> {text}")
        else:
            print(f"  已有代码: P{i} -> {text}")

print(f"\n缺少代码的截图: {len(missing_code_after)}个")

# ──────────────────────────────────────────
# 准备需要添加的代码
# ──────────────────────────────────────────

# 图5.5 工作人员模块 -> 添加订单条件查询代码
staff_code = [
    '@GetMapping("/page/condition")',
    'public Result<PageResult<RentalOrder>>',
    '        pageByCondition(',
    '        @RequestParam(required = false)',
    '            String orderNo,',
    '        @RequestParam(required = false)',
    '            Integer status,',
    '        @RequestParam(defaultValue = "1")',
    '            Integer pageNum,',
    '        @RequestParam(defaultValue = "10")',
    '            Integer pageSize) {',
    '    PageResult<RentalOrder> result =',
    '        rentalOrderService.pageByCondition(',
    '            orderNo, null, null,',
    '            status, pageNum, pageSize);',
    '    return Result.success(result);',
    '}',
]

# 图5.6 数据统计 -> 添加Dashboard概览查询代码
dashboard_code = [
    '@GetMapping("/overview")',
    'public Result<Map<String, Object>>',
    '        getOverview() {',
    '    Map<String, Object> overview =',
    '        new HashMap<>();',
    '    List<User> users =',
    '        userMapper.selectAll();',
    '    long totalUsers = users.stream()',
    '        .filter(u -> "user"',
    '            .equals(u.getRole()))',
    '        .count();',
    '    overview.put("totalUsers", totalUsers);',
    '    List<RentalOrder> orders =',
    '        rentalOrderMapper.selectAll();',
    '    long todayOrders = orders.stream()',
    '        .filter(o -> isToday(',
    '            o.getCreateTime()))',
    '        .count();',
    '    overview.put("todayOrders",',
    '        todayOrders);',
    '    return Result.success(overview);',
    '}',
]

# 按索引从大到小处理（避免索引偏移）
missing_code_after.sort(key=lambda x: x[0], reverse=True)

for idx, title in missing_code_after:
    p = paragraphs[idx]
    if '5.5' in title or '工作人员' in title:
        print(f"\n在 P{idx} '{title}' 后添加工作人员代码 ({len(staff_code)}行)")
        insert_code_block_after(p, staff_code)
    elif '5.6' in title or '数据' in title or '统计' in title:
        print(f"\n在 P{idx} '{title}' 后添加数据统计代码 ({len(dashboard_code)}行)")
        insert_code_block_after(p, dashboard_code)
    else:
        print(f"\n  跳过: P{idx} '{title}' (未匹配到对应代码)")

# 保存
try:
    doc.save(dst)
    print(f"\n修改完成！已保存到: {dst}")
except PermissionError:
    alt_dst = dst.replace('.docx', '_updated.docx')
    doc.save(alt_dst)
    print(f"\n原文件被占用，已保存到: {alt_dst}")
