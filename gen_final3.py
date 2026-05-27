# -*- coding: utf-8 -*-
import sys
sys.stdout.reconfigure(encoding='utf-8')
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
from matplotlib.patches import Rectangle, Ellipse
from matplotlib.font_manager import FontProperties

plt.rcParams['axes.unicode_minus'] = False
FONT = r'C:\Windows\Fonts\simsun.ttc'
OUT = r"F:\26毕设单\基于springboot的飞跃滑雪场管理系统的设计与实现"
def mkf(sz): return FontProperties(fname=FONT, size=sz)


def structure():
    fig, ax = plt.subplots(figsize=(20, 16), dpi=150)
    ax.axis('off')
    fig.patch.set_facecolor('white')

    def box(x, y, w, h, text, fs=24, vert=False):
        ax.add_patch(Rectangle((x-w/2, y-h/2), w, h, fc='white', ec='black', lw=1.8))
        t = "\n".join(text) if vert else text
        ax.text(x, y, t, ha='center', va='center', fontproperties=mkf(fs), linespacing=1.15)

    def ln(x1, y1, x2, y2):
        ax.plot([x1, x2], [y1, y2], 'k-', lw=1.3)

    # Calculate positions - compact layout with small group gaps
    sp = 1.4
    gg = 0.7  # group gap
    fw = 1.15

    groups = [
        ("管理员", ["用户管理", "教练管理", "课程管理", "场地管理", "商品管理", "数据统计"]),
        ("教  练", ["我的课程", "学员预约"]),
        ("工作人员", ["商品管理", "租赁订单", "场地信息"]),
        ("普通用户", ["商品租赁", "教练预约", "课程报名", "场地预约", "个人中心"]),
    ]

    # Calculate x positions for all function boxes
    all_positions = []
    role_centers = []
    x = 0.5
    for gi, (rname, flist) in enumerate(groups):
        if gi > 0:
            x += gg
        positions = []
        for i in range(len(flist)):
            positions.append(x)
            if i < len(flist) - 1:
                x += sp
        all_positions.append(positions)
        role_centers.append((positions[0] + positions[-1]) / 2)
        x += sp  # move past last item

    total_w = x - sp + 0.5
    ax.set_xlim(-0.2, total_w + 0.2)
    ax.set_ylim(-0.5, 16)

    root_cx = total_w / 2

    # Root
    box(root_cx, 14.5, 7.5, 1.3, "飞跃滑雪场管理系统", fs=26)

    # Roles
    ry = 11.8
    rw, rh = 2.8, 1.0
    for rc, (rname, _) in zip(role_centers, groups):
        box(rc, ry, rw, rh, rname, fs=24)
        ln(root_cx, 13.9, rc, ry + rh/2)

    # Functions
    bar_y = 9.8
    for rc, positions, (_, flist) in zip(role_centers, all_positions, groups):
        ln(rc, ry - rh/2, rc, bar_y)
        if len(positions) > 1:
            ln(positions[0], bar_y, positions[-1], bar_y)
        for fx, fname in zip(positions, flist):
            ln(fx, bar_y, fx, bar_y - 0.2)
            nch = len(fname)
            fh = nch * 1.0 + 0.5
            by = bar_y - 0.2 - fh/2
            box(fx, by, fw, fh, fname, fs=24, vert=True)

    fig.savefig(f"{OUT}\\structure_chart.png", bbox_inches='tight', dpi=150, facecolor='white')
    plt.close()
    print("Structure done")


def login_flow():
    fig, ax = plt.subplots(figsize=(12, 20), dpi=150)
    ax.axis('off')
    fig.patch.set_facecolor('white')

    fs = 24
    f, f_s = mkf(fs), mkf(fs-2)
    bw, bh = 4, 0.8
    cx = 3.8
    err_x = 7.2
    err_w = 2.2
    ds = 0.7
    oval_h = 0.375

    ax.set_xlim(-0.5, 9)
    ax.set_ylim(-1, 16)

    def rect(x, y, text, w=bw):
        ax.add_patch(Rectangle((x-w/2, y-bh/2), w, bh, fc='white', ec='black', lw=1.3))
        ax.text(x, y, text, ha='center', va='center', fontproperties=f)

    def diam(x, y, text):
        pts = [(x, y+ds), (x+ds*1.4, y), (x, y-ds), (x-ds*1.4, y)]
        ax.add_patch(plt.Polygon(pts, fc='white', ec='black', lw=1.3))
        ax.text(x, y, text, ha='center', va='center', fontproperties=f)

    def draw_oval(x, y, text):
        ax.add_patch(Ellipse((x, y), 2.2, 0.75, fc='white', ec='black', lw=1.3))
        ax.text(x, y, text, ha='center', va='center', fontproperties=f)

    def varr(x, y_from, y_to):
        ax.annotate('', xy=(x, y_to), xytext=(x, y_from),
                    arrowprops=dict(arrowstyle='->', color='black', lw=1.3,
                                   shrinkA=0, shrinkB=0))

    def harr(x_from, y, x_to):
        ax.annotate('', xy=(x_to, y), xytext=(x_from, y),
                    arrowprops=dict(arrowstyle='->', color='black', lw=1.3,
                                   shrinkA=0, shrinkB=0))

    # Layout: define all y positions first
    positions = []
    y = 15.0
    positions.append(('oval', y, "开始"))
    y -= 1.6; positions.append(('rect', y, "用户输入用户名和密码"))
    y -= 1.6; positions.append(('diam', y, "表单校验"))
    y -= 1.6; positions.append(('rect', y, "发送登录请求到后端"))
    y -= 1.6; positions.append(('rect', y, "后端查询数据库验证"))
    y -= 1.6; positions.append(('diam', y, "验证通过"))
    y -= 1.6; positions.append(('rect', y, "生成JWT Token"))
    y -= 1.6; positions.append(('rect', y, "前端存储Token"))
    y -= 1.6; positions.append(('rect', y, "根据角色跳转对应首页"))
    y -= 1.2; positions.append(('oval', y, "结束"))

    for i, (shape, y, text) in enumerate(positions):
        if shape == 'oval':
            draw_oval(cx, y, text)
        elif shape == 'rect':
            rect(cx, y, text)
        elif shape == 'diam':
            diam(cx, y, text)

    # Draw arrows between consecutive shapes
    for i in range(len(positions) - 1):
        s1_shape, s1_y, _ = positions[i]
        s2_shape, s2_y, _ = positions[i+1]

        # Bottom of current shape
        if s1_shape == 'oval':
            y_from = s1_y - oval_h
        elif s1_shape == 'rect':
            y_from = s1_y - bh/2
        elif s1_shape == 'diam':
            y_from = s1_y - ds

        # Top of next shape
        if s2_shape == 'oval':
            y_to = s2_y + oval_h
        elif s2_shape == 'rect':
            y_to = s2_y + bh/2
        elif s2_shape == 'diam':
            y_to = s2_y + ds

        varr(cx, y_from, y_to)

    # Error branches
    # Diamond 1: 表单校验 (index 2)
    d1_y = positions[2][1]
    harr(cx + ds*1.4, d1_y, err_x - err_w/2)
    rect(err_x, d1_y, "提示错误", w=err_w)
    ax.text(cx + ds*1.4 + 0.1, d1_y + 0.2, "不通过", fontproperties=f_s)
    ax.text(cx + 0.1, d1_y - ds - 0.2, "通过", fontproperties=f_s)

    # Diamond 2: 验证通过 (index 5)
    d2_y = positions[5][1]
    harr(cx + ds*1.4, d2_y, err_x - err_w/2)
    rect(err_x, d2_y, "返回错误信息", w=err_w)
    ax.text(cx + ds*1.4 + 0.1, d2_y + 0.2, "否", fontproperties=f_s)
    ax.text(cx + 0.1, d2_y - ds - 0.2, "是", fontproperties=f_s)

    fig.savefig(f"{OUT}\\login_flow.png", bbox_inches='tight', dpi=150, facecolor='white')
    plt.close()
    print("Login flow done")


def rental_flow():
    fig, ax = plt.subplots(figsize=(12, 22), dpi=150)
    ax.axis('off')
    fig.patch.set_facecolor('white')

    fs = 24
    f, f_s = mkf(fs), mkf(fs-2)
    bw, bh = 4, 0.8
    cx = 3.8
    err_x = 7.2
    err_w = 2.2
    ds = 0.7
    oval_h = 0.375

    ax.set_xlim(-0.5, 9)
    ax.set_ylim(-2, 17)

    def rect(x, y, text, w=bw):
        ax.add_patch(Rectangle((x-w/2, y-bh/2), w, bh, fc='white', ec='black', lw=1.3))
        ax.text(x, y, text, ha='center', va='center', fontproperties=f)

    def diam(x, y, text):
        pts = [(x, y+ds), (x+ds*1.4, y), (x, y-ds), (x-ds*1.4, y)]
        ax.add_patch(plt.Polygon(pts, fc='white', ec='black', lw=1.3))
        ax.text(x, y, text, ha='center', va='center', fontproperties=f)

    def draw_oval(x, y, text):
        ax.add_patch(Ellipse((x, y), 2.2, 0.75, fc='white', ec='black', lw=1.3))
        ax.text(x, y, text, ha='center', va='center', fontproperties=f)

    def varr(x, y_from, y_to):
        ax.annotate('', xy=(x, y_to), xytext=(x, y_from),
                    arrowprops=dict(arrowstyle='->', color='black', lw=1.3,
                                   shrinkA=0, shrinkB=0))

    def harr(x_from, y, x_to):
        ax.annotate('', xy=(x_to, y), xytext=(x_from, y),
                    arrowprops=dict(arrowstyle='->', color='black', lw=1.3,
                                   shrinkA=0, shrinkB=0))

    g = 1.4
    positions = []
    y = 16.0
    positions.append(('oval', y, "开始"))
    y -= g; positions.append(('rect', y, "用户浏览商品列表"))
    y -= g; positions.append(('rect', y, "选择商品，填写租赁信息"))
    y -= g; positions.append(('rect', y, "提交租赁订单"))
    y -= g; positions.append(('diam', y, "库存充足"))
    y -= g; positions.append(('rect', y, "生成订单，扣减库存"))
    y -= g; positions.append(('rect', y, "用户使用租赁商品"))
    y -= g; positions.append(('rect', y, "用户归还商品"))
    y -= g; positions.append(('rect', y, "工作人员确认归还"))
    y -= g; positions.append(('rect', y, "更新订单状态为已归还"))
    y -= g; positions.append(('rect', y, "恢复商品可用库存"))
    y -= g*0.8; positions.append(('oval', y, "结束"))

    for shape, y, text in positions:
        if shape == 'oval': draw_oval(cx, y, text)
        elif shape == 'rect': rect(cx, y, text)
        elif shape == 'diam': diam(cx, y, text)

    for i in range(len(positions) - 1):
        s1, y1, _ = positions[i]
        s2, y2, _ = positions[i+1]
        yf = y1 - (oval_h if s1=='oval' else (ds if s1=='diam' else bh/2))
        yt = y2 + (oval_h if s2=='oval' else (ds if s2=='diam' else bh/2))
        varr(cx, yf, yt)

    # Error branch
    d_y = positions[4][1]
    harr(cx + ds*1.4, d_y, err_x - err_w/2)
    rect(err_x, d_y, "提示库存不足", w=err_w)
    ax.text(cx + ds*1.4 + 0.1, d_y + 0.2, "否", fontproperties=f_s)
    ax.text(cx + 0.1, d_y - ds - 0.2, "是", fontproperties=f_s)

    fig.savefig(f"{OUT}\\rental_flow.png", bbox_inches='tight', dpi=150, facecolor='white')
    plt.close()
    print("Rental flow done")


structure()
login_flow()
rental_flow()
print("All done!")
