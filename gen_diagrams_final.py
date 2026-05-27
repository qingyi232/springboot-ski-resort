# -*- coding: utf-8 -*-
"""
纯白背景 + 宋体字 + 黑色边框的学术风格图表
"""
import sys
sys.stdout.reconfigure(encoding='utf-8')
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
from matplotlib.patches import FancyBboxPatch, Rectangle
from matplotlib.font_manager import FontProperties
import numpy as np

# 使用宋体
font = FontProperties(fname=r'C:\Windows\Fonts\simsun.ttc', size=10)
font_bold = FontProperties(fname=r'C:\Windows\Fonts\simsun.ttc', size=10)
plt.rcParams['axes.unicode_minus'] = False

OUT = r"F:\26毕设单\基于springboot的飞跃滑雪场管理系统的设计与实现"

WHITE = 'white'
BLACK = 'black'


# ============================================================
# 1. 系统功能结构图 - 竖排文字，纯白底
# ============================================================
def draw_structure():
    fig, ax = plt.subplots(1, 1, figsize=(18, 9), dpi=200)
    ax.set_xlim(0.5, 19.5)
    ax.set_ylim(3, 11)
    ax.axis('off')
    fig.patch.set_facecolor('white')

    def box(x, y, w, h, text, fs=10, vertical=False):
        rect = Rectangle((x-w/2, y-h/2), w, h, facecolor=WHITE, edgecolor=BLACK, linewidth=1.2)
        ax.add_patch(rect)
        t = "\n".join(text) if vertical else text
        f = FontProperties(fname=r'C:\Windows\Fonts\simsun.ttc', size=fs)
        ax.text(x, y, t, ha='center', va='center', fontproperties=f, linespacing=1.15)

    def line(x1, y1, x2, y2):
        ax.plot([x1, x2], [y1, y2], color=BLACK, linewidth=1.0)

    # Root
    box(10, 10.2, 5.5, 0.8, "飞跃滑雪场管理系统", fs=13)

    # Roles
    role_y = 8.5
    roles = [(3.5, "管理员"), (8, "教  练"), (12.5, "工作人员"), (17, "普通用户")]
    for rx, rn in roles:
        box(rx, role_y, 2.2, 0.65, rn, fs=11)
        line(10, 9.8, rx, role_y + 0.325)

    # Functions (vertical text)
    funcs = {
        3.5: ["用户管理", "教练管理", "课程管理", "场地管理", "商品管理", "数据统计"],
        8: ["我的课程", "学员预约"],
        12.5: ["商品管理", "租赁订单", "场地信息"],
        17: ["商品租赁", "教练预约", "课程报名", "场地预约", "个人中心"],
    }

    sp = 1.05
    bar_y = 7.3
    func_h = 2.8
    func_w = 0.7
    func_top = bar_y - 0.15

    for rx, flist in funcs.items():
        n = len(flist)
        total = (n-1) * sp
        start = rx - total/2
        
        line(rx, role_y - 0.325, rx, bar_y)
        if n > 1:
            line(start, bar_y, start + (n-1)*sp, bar_y)
        
        for i, fname in enumerate(flist):
            fx = start + i * sp
            line(fx, bar_y, fx, func_top)
            nch = len(fname)
            bh = max(nch * 0.5 + 0.3, 1.8)
            by = func_top - bh/2
            box(fx, by, func_w, bh, fname, fs=10, vertical=True)

    fig.savefig(f"{OUT}\\structure_chart.png", bbox_inches='tight', dpi=200, facecolor='white')
    plt.close()
    print("Structure chart done")


# ============================================================
# 2. 用户登录流程图 - 纯白底
# ============================================================
def draw_login():
    fig, ax = plt.subplots(1, 1, figsize=(8, 15), dpi=200)
    ax.set_xlim(0, 8)
    ax.set_ylim(-1.5, 15)
    ax.axis('off')
    fig.patch.set_facecolor('white')

    fs = 10.5
    f = FontProperties(fname=r'C:\Windows\Fonts\simsun.ttc', size=fs)
    f_sm = FontProperties(fname=r'C:\Windows\Fonts\simsun.ttc', size=fs-2)
    bw, bh = 3.2, 0.7

    def rect(cx, cy, text):
        r = Rectangle((cx-bw/2, cy-bh/2), bw, bh, facecolor=WHITE, edgecolor=BLACK, lw=1.2)
        ax.add_patch(r)
        ax.text(cx, cy, text, ha='center', va='center', fontproperties=f)

    def diamond(cx, cy, text):
        s = 0.65
        pts = [(cx, cy+s), (cx+s*1.5, cy), (cx, cy-s), (cx-s*1.5, cy)]
        d = plt.Polygon(pts, facecolor=WHITE, edgecolor=BLACK, lw=1.2)
        ax.add_patch(d)
        ax.text(cx, cy, text, ha='center', va='center', fontproperties=f)

    def oval(cx, cy, text):
        from matplotlib.patches import Ellipse
        e = Ellipse((cx, cy), 2, 0.65, facecolor=WHITE, edgecolor=BLACK, lw=1.2)
        ax.add_patch(e)
        ax.text(cx, cy, text, ha='center', va='center', fontproperties=f)

    def arr(x1, y1, x2, y2):
        ax.annotate('', xy=(x2, y2), xytext=(x1, y1),
                    arrowprops=dict(arrowstyle='->', color=BLACK, lw=1.2))

    def label(x, y, text):
        ax.text(x, y, text, fontproperties=f_sm, color=BLACK)

    cx = 3.5
    g = 1.6

    y = 14; oval(cx, y, "开始")
    arr(cx, y-0.33, cx, y-0.8)

    y -= g; rect(cx, y, "用户输入用户名和密码")
    arr(cx, y-bh/2, cx, y-bh/2-0.55)

    y -= g; diamond(cx, y, "表单校验")
    label(cx+0.1, y-0.85, "通过")
    label(cx+1.1, y+0.15, "不通过")
    arr(cx+0.65*1.5, y, 6.5, y)
    rect(6.5, y, "提示错误")
    arr(cx, y-0.65, cx, y-0.65-0.55)

    y -= g; rect(cx, y, "发送登录请求到后端")
    arr(cx, y-bh/2, cx, y-bh/2-0.55)

    y -= g; rect(cx, y, "后端查询数据库验证")
    arr(cx, y-bh/2, cx, y-bh/2-0.55)

    y -= g; diamond(cx, y, "验证通过")
    label(cx+0.1, y-0.85, "是")
    label(cx+1.1, y+0.15, "否")
    arr(cx+0.65*1.5, y, 6.5, y)
    rect(6.5, y, "返回错误信息")
    arr(cx, y-0.65, cx, y-0.65-0.55)

    y -= g; rect(cx, y, "生成JWT Token")
    arr(cx, y-bh/2, cx, y-bh/2-0.55)

    y -= g; rect(cx, y, "前端存储Token")
    arr(cx, y-bh/2, cx, y-bh/2-0.55)

    y -= g; rect(cx, y, "根据角色跳转对应首页")
    arr(cx, y-bh/2, cx, y-bh/2-0.55)

    y -= g*0.7; oval(cx, y, "结束")

    fig.savefig(f"{OUT}\\login_flow.png", bbox_inches='tight', dpi=200, facecolor='white')
    plt.close()
    print("Login flow done")


# ============================================================
# 3. 商品租赁业务流程图 - 纯白底
# ============================================================
def draw_rental():
    fig, ax = plt.subplots(1, 1, figsize=(8, 17), dpi=200)
    ax.set_xlim(0, 8)
    ax.set_ylim(-2.5, 16)
    ax.axis('off')
    fig.patch.set_facecolor('white')

    fs = 10.5
    f = FontProperties(fname=r'C:\Windows\Fonts\simsun.ttc', size=fs)
    f_sm = FontProperties(fname=r'C:\Windows\Fonts\simsun.ttc', size=fs-2)
    bw, bh = 3.2, 0.7

    def rect(cx, cy, text):
        r = Rectangle((cx-bw/2, cy-bh/2), bw, bh, facecolor=WHITE, edgecolor=BLACK, lw=1.2)
        ax.add_patch(r)
        ax.text(cx, cy, text, ha='center', va='center', fontproperties=f)

    def diamond(cx, cy, text):
        s = 0.65
        pts = [(cx, cy+s), (cx+s*1.5, cy), (cx, cy-s), (cx-s*1.5, cy)]
        d = plt.Polygon(pts, facecolor=WHITE, edgecolor=BLACK, lw=1.2)
        ax.add_patch(d)
        ax.text(cx, cy, text, ha='center', va='center', fontproperties=f)

    def oval(cx, cy, text):
        from matplotlib.patches import Ellipse
        e = Ellipse((cx, cy), 2, 0.65, facecolor=WHITE, edgecolor=BLACK, lw=1.2)
        ax.add_patch(e)
        ax.text(cx, cy, text, ha='center', va='center', fontproperties=f)

    def arr(x1, y1, x2, y2):
        ax.annotate('', xy=(x2, y2), xytext=(x1, y1),
                    arrowprops=dict(arrowstyle='->', color=BLACK, lw=1.2))

    def label(x, y, text):
        ax.text(x, y, text, fontproperties=f_sm, color=BLACK)

    cx = 3.5
    g = 1.4

    y = 15; oval(cx, y, "开始")
    arr(cx, y-0.33, cx, y-0.75)

    y -= g; rect(cx, y, "用户浏览商品列表")
    arr(cx, y-bh/2, cx, y-bh/2-0.45)

    y -= g; rect(cx, y, "选择商品，填写租赁信息")
    arr(cx, y-bh/2, cx, y-bh/2-0.45)

    y -= g; rect(cx, y, "提交租赁订单")
    arr(cx, y-bh/2, cx, y-bh/2-0.45)

    y -= g; diamond(cx, y, "库存充足")
    label(cx+0.1, y-0.85, "是")
    label(cx+1.1, y+0.15, "否")
    arr(cx+0.65*1.5, y, 6.5, y)
    rect(6.5, y, "提示库存不足")
    arr(cx, y-0.65, cx, y-0.65-0.45)

    y -= g; rect(cx, y, "生成订单，扣减库存")
    arr(cx, y-bh/2, cx, y-bh/2-0.45)

    y -= g; rect(cx, y, "用户使用租赁商品")
    arr(cx, y-bh/2, cx, y-bh/2-0.45)

    y -= g; rect(cx, y, "用户归还商品")
    arr(cx, y-bh/2, cx, y-bh/2-0.45)

    y -= g; rect(cx, y, "工作人员确认归还")
    arr(cx, y-bh/2, cx, y-bh/2-0.45)

    y -= g; rect(cx, y, "更新订单状态为已归还")
    arr(cx, y-bh/2, cx, y-bh/2-0.45)

    y -= g; rect(cx, y, "恢复商品可用库存")
    arr(cx, y-bh/2, cx, y-bh/2-0.45)

    y -= g*0.6; oval(cx, y, "结束")

    fig.savefig(f"{OUT}\\rental_flow.png", bbox_inches='tight', dpi=200, facecolor='white')
    plt.close()
    print("Rental flow done")


draw_structure()
draw_login()
draw_rental()
print("All diagrams generated!")
