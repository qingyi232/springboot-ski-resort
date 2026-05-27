"""
修改 5.25_fixed.docx：
1. 每部分代码扩展到18-20行
2. 每部分代码前加"核心代码："
3. 代码行距与正文行距一致
"""
import docx
from docx.oxml.ns import qn, nsdecls
from docx.oxml import parse_xml
from docx.shared import Pt, Twips
from copy import deepcopy
import sys

sys.stdout.reconfigure(encoding='utf-8')

src = r'F:\26毕设单\基于springboot的飞跃滑雪场管理系统的设计与实现\5.25_fixed.docx'
dst = r'F:\26毕设单\基于springboot的飞跃滑雪场管理系统的设计与实现\5.25_v4.docx'

doc = docx.Document(src)
paragraphs = doc.paragraphs

def escape_xml(text):
    return text.replace('&', '&amp;').replace('<', '&lt;').replace('>', '&gt;').replace('"', '&quot;')

def get_body_line_spacing():
    """获取正文的行距设置"""
    for p in paragraphs:
        if p.style and p.style.name == 'Normal' and len(p.text) > 50:
            pPr = p._element.find(qn('w:pPr'))
            if pPr is not None:
                spacing = pPr.find(qn('w:spacing'))
                if spacing is not None:
                    return deepcopy(spacing)
    return None

body_spacing = get_body_line_spacing()
print(f"正文行距XML: {body_spacing.attrib if body_spacing is not None else 'None'}")

def create_code_paragraph(text, ref_p):
    """创建一个代码段落，行距与正文一致"""
    new_p = deepcopy(ref_p._element)
    # 清除所有run
    for r in new_p.findall(qn('w:r')):
        new_p.remove(r)
    
    # 设置行距与正文一致
    pPr = new_p.find(qn('w:pPr'))
    if pPr is None:
        pPr = parse_xml(f'<w:pPr {nsdecls("w")}></w:pPr>')
        new_p.insert(0, pPr)
    
    # 移除旧的spacing
    old_spacing = pPr.find(qn('w:spacing'))
    if old_spacing is not None:
        pPr.remove(old_spacing)
    
    # 添加正文的spacing
    if body_spacing is not None:
        pPr.append(deepcopy(body_spacing))
    
    # 获取参考段落的run格式
    ref_runs = ref_p._element.findall(qn('w:r'))
    rPr_copy = None
    if ref_runs:
        rPr = ref_runs[0].find(qn('w:rPr'))
        if rPr is not None:
            rPr_copy = deepcopy(rPr)
    
    # 创建新run
    new_r = parse_xml(f'<w:r {nsdecls("w")}><w:t xml:space="preserve">{escape_xml(text)}</w:t></w:r>')
    if rPr_copy is not None:
        new_r.insert(0, rPr_copy)
    new_p.append(new_r)
    return new_p

def create_label_paragraph(text, ref_p):
    """创建"核心代码："标签段落，使用正文格式"""
    new_p = deepcopy(ref_p._element)
    for r in new_p.findall(qn('w:r')):
        new_p.remove(r)
    
    pPr = new_p.find(qn('w:pPr'))
    if pPr is None:
        pPr = parse_xml(f'<w:pPr {nsdecls("w")}></w:pPr>')
        new_p.insert(0, pPr)
    
    old_spacing = pPr.find(qn('w:spacing'))
    if old_spacing is not None:
        pPr.remove(old_spacing)
    if body_spacing is not None:
        pPr.append(deepcopy(body_spacing))
    
    ref_runs = ref_p._element.findall(qn('w:r'))
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

def replace_code_block(start_idx, end_idx, new_lines, add_label=True):
    """替换代码块：删除旧行，插入新行"""
    body = doc.element.body
    
    # 找到正文描述段落（代码块前面的非代码段落）作为格式参考
    ref_p = paragraphs[start_idx]
    # 找正文段落作为"核心代码："的格式参考
    desc_ref = None
    for i in range(start_idx - 1, max(0, start_idx - 5), -1):
        if not is_code(paragraphs[i].text) and len(paragraphs[i].text) > 10:
            desc_ref = paragraphs[i]
            break
    if desc_ref is None:
        desc_ref = ref_p
    
    # 获取第一个代码段落在body中的位置
    first_elem = paragraphs[start_idx]._element
    
    # 如果需要加"核心代码："标签
    if add_label:
        label_p = create_label_paragraph("核心代码：", desc_ref)
        first_elem.addprevious(label_p)
    
    # 删除旧的代码段落（从后往前删，避免索引问题）
    for i in range(end_idx, start_idx - 1, -1):
        elem = paragraphs[i]._element
        elem.getparent().remove(elem)
    
    # 在标签后面（或原位置）插入新代码行
    if add_label:
        insert_after = label_p
    else:
        # 找到原位置的前一个元素
        insert_after = label_p if add_label else first_elem.getprevious()
    
    current = insert_after
    for line in new_lines:
        new_elem = create_code_paragraph(line, ref_p)
        current.addnext(new_elem)
        current = new_elem

def is_code(text):
    text = text.strip()
    code_signs = ['@PostMapping', '@GetMapping', '@PutMapping', '@DeleteMapping',
                  '@RequestMapping', '@RequestParam', '@PathVariable', '@RequestBody',
                  'public ', 'private ', 'return ', 'void ', 'router.',
                  'const ', 'import ', 'Map<', 'List<', 'Result<',
                  '.put(', '.get(', '.set(', 'next(', 'new ',
                  'claims.', 'data.', 'userStore.', 'Jwts.', 'String ',
                  'Long ', 'Integer ', 'coach', 'booking', 'order',
                  '    ', '{', '}', '=>', 'if (', 'else']
    if not text:
        return False
    for s in code_signs:
        if s in text:
            return True
    if text in ['}', '});', '})', '})']:
        return True
    return False

# ──────────────────────────────────────────
# 定位所有代码块
# ──────────────────────────────────────────
code_blocks = {}
for i, p in enumerate(paragraphs):
    t = p.text.strip()
    if t == '@PostMapping("/login")':
        code_blocks['login_start'] = i
    if 'login_start' in code_blocks and 'login_end' not in code_blocks:
        if t == '}' and i > code_blocks['login_start'] and i <= code_blocks['login_start'] + 15:
            code_blocks['login_end'] = i

    if 'public static String generateToken' in t:
        code_blocks['jwt_start'] = i
    if 'jwt_start' in code_blocks and 'jwt_end' not in code_blocks:
        if t == '}' and i > code_blocks['jwt_start'] and i <= code_blocks['jwt_start'] + 15:
            code_blocks['jwt_end'] = i

    if t == '@GetMapping("/page/condition")':
        code_blocks['product_start'] = i
    if 'product_start' in code_blocks and 'product_end' not in code_blocks:
        if t == '}' and i > code_blocks['product_start'] and i <= code_blocks['product_start'] + 15:
            code_blocks['product_end'] = i

    if t == '@GetMapping("/my-bookings")':
        code_blocks['coach_start'] = i
    if 'coach_start' in code_blocks and 'coach_end' not in code_blocks:
        if t == '}' and i > code_blocks['coach_start'] and i <= code_blocks['coach_start'] + 15:
            code_blocks['coach_end'] = i

    if '@PutMapping("/{id}/return")' in t:
        code_blocks['return_idx'] = i

    if 'router.beforeEach' in t:
        code_blocks['router_start'] = i
    if 'router_start' in code_blocks and 'router_end' not in code_blocks:
        if t == '})' and i > code_blocks['router_start']:
            code_blocks['router_end'] = i

print("代码块位置:")
for k, v in code_blocks.items():
    print(f"  {k}: P{v} -> {paragraphs[v].text[:50]}")

# ──────────────────────────────────────────
# 新代码内容（每块18-20行）
# ──────────────────────────────────────────

login_code = [
    '@PostMapping("/login")',
    'public Result<Map<String, Object>> login(',
    '        @RequestBody LoginDTO loginDTO) {',
    '    String username = loginDTO.getUsername();',
    '    String password = loginDTO.getPassword();',
    '    User user = userService.findByUsername(username);',
    '    if (user == null) {',
    '        return Result.error("用户名不存在");',
    '    }',
    '    if (!passwordEncoder.matches(',
    '            password, user.getPassword())) {',
    '        return Result.error("密码错误");',
    '    }',
    '    String token = JwtUtil.generateToken(',
    '        user.getId(), user.getUsername(),',
    '        user.getRole());',
    '    Map<String, Object> data = new HashMap<>();',
    '    data.put("token", token);',
    '    data.put("userInfo", user);',
    '    return Result.success("登录成功", data);',
    '}',
]

jwt_code = [
    'public static String generateToken(',
    '        Long userId, String username,',
    '        String role) {',
    '    Map<String, Object> claims = new HashMap<>();',
    '    claims.put("userId", userId);',
    '    claims.put("username", username);',
    '    claims.put("role", role);',
    '    Date issuedAt = new Date();',
    '    long expireMillis = 7 * 24 * 3600 * 1000L;',
    '    Date expiration = new Date(',
    '        issuedAt.getTime() + expireMillis);',
    '    return Jwts.builder()',
    '        .setClaims(claims)',
    '        .setSubject(username)',
    '        .setIssuedAt(issuedAt)',
    '        .setExpiration(expiration)',
    '        .signWith(getSecretKey(),',
    '            SignatureAlgorithm.HS256)',
    '        .compact();',
    '}',
]

product_code = [
    '@GetMapping("/page/condition")',
    'public Result<PageResult<Product>> getByCondition(',
    '        @RequestParam(defaultValue = "1")',
    '            Integer page,',
    '        @RequestParam(defaultValue = "10")',
    '            Integer pageSize,',
    '        @RequestParam(required = false)',
    '            String name,',
    '        @RequestParam(required = false)',
    '            String category) {',
    '    PageResult<Product> result =',
    '        productService.pageByCondition(',
    '            page, pageSize, name, category);',
    '    if (result.getList() != null) {',
    '        result.getList().forEach(p -> {',
    '            p.setPassword(null);',
    '        });',
    '    }',
    '    return Result.success(result);',
    '}',
]

coach_code = [
    '@GetMapping("/my-bookings")',
    'public Result<List<CoachBooking>>',
    '        getMyBookings() {',
    '    Long userId =',
    '        SecurityUtils.getCurrentUserId();',
    '    Coach coach =',
    '        coachService.getByUserId(userId);',
    '    if (coach == null) {',
    '        return Result.error(',
    '            "教练信息不存在");',
    '    }',
    '    List<CoachBooking> bookings =',
    '        coachBookingService',
    '            .getByCoachId(coach.getId());',
    '    bookings.sort((a, b) ->',
    '        b.getBookingDate().compareTo(',
    '            a.getBookingDate()));',
    '    return Result.success(bookings);',
    '}',
]

return_code = [
    '@PutMapping("/{id}/return")',
    'public Result<Boolean> returnEquipment(',
    '        @PathVariable Long id) {',
    '    RentalOrder order =',
    '        rentalOrderService.getById(id);',
    '    if (order == null) {',
    '        return Result.error("订单不存在");',
    '    }',
    '    if (order.getOrderStatus() != 1) {',
    '        return Result.error(',
    '            "当前状态不允许归还");',
    '    }',
    '    order.setOrderStatus(2);',
    '    order.setRentalEndTime(new Date());',
    '    Product product = productService',
    '        .getById(order.getProductId());',
    '    product.setAvailableQuantity(',
    '        product.getAvailableQuantity() + 1);',
    '    productService.update(product);',
    '    rentalOrderService.update(order);',
    '    return Result.success(true);',
    '}',
]

router_code = [
    'router.beforeEach((to, from, next) => {',
    '  const userStore = useUserStore()',
    '  const token = userStore.token',
    '  const role = userStore.userInfo?.role',
    '  if (to.meta.requiresAuth && !token) {',
    "    ElMessage.warning('请先登录')",
    "    next('/login')",
    '    return',
    '  }',
    '  if (to.meta.roles &&',
    '      !to.meta.roles.includes(role)) {',
    "    ElMessage.error('无权限访问')",
    "    const target = role === 'user'",
    "      ? '/home'",
    "      : '/admin/dashboard'",
    '    next(target)',
    '    return',
    '  }',
    '  next()',
    '})',
]

# ──────────────────────────────────────────
# 依次处理每个代码块（从后往前，避免索引偏移）
# ──────────────────────────────────────────
# 按段落索引从大到小排列处理顺序
blocks_to_process = []

if 'router_start' in code_blocks and 'router_end' in code_blocks:
    blocks_to_process.append(('Router', code_blocks['router_start'], code_blocks['router_end'], router_code))

if 'return_idx' in code_blocks:
    blocks_to_process.append(('Return', code_blocks['return_idx'], code_blocks['return_idx'], return_code))

if 'coach_start' in code_blocks and 'coach_end' in code_blocks:
    blocks_to_process.append(('Coach', code_blocks['coach_start'], code_blocks['coach_end'], coach_code))

if 'product_start' in code_blocks and 'product_end' in code_blocks:
    blocks_to_process.append(('Product', code_blocks['product_start'], code_blocks['product_end'], product_code))

if 'jwt_start' in code_blocks and 'jwt_end' in code_blocks:
    blocks_to_process.append(('JWT', code_blocks['jwt_start'], code_blocks['jwt_end'], jwt_code))

if 'login_start' in code_blocks and 'login_end' in code_blocks:
    blocks_to_process.append(('Login', code_blocks['login_start'], code_blocks['login_end'], login_code))

# 按start_idx从大到小排序
blocks_to_process.sort(key=lambda x: x[1], reverse=True)

for name, start, end, new_lines in blocks_to_process:
    old_count = end - start + 1
    print(f"\n处理 {name}: P{start}-P{end} ({old_count}行) -> {len(new_lines)}行")
    replace_code_block(start, end, new_lines, add_label=True)
    print(f"  完成: 添加'核心代码：'标签 + {len(new_lines)}行代码")

# ──────────────────────────────────────────
# 额外：设置所有已有代码段落的行距
# ──────────────────────────────────────────
# 重新加载段落（因为已经修改了DOM）
# 这步在上面的create_code_paragraph中已经处理了

doc.save(dst)
print(f"\n修改完成！已保存到: {dst}")
