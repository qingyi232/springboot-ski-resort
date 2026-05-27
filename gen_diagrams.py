# -*- coding: utf-8 -*-
"""
生成系统功能结构图和业务流程图
文字竖排，字体10pt左右
"""
import sys
sys.stdout.reconfigure(encoding='utf-8')
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.patches as patches
from matplotlib.patches import FancyBboxPatch, FancyArrowPatch
import numpy as np

plt.rcParams['font.sans-serif'] = ['SimHei', 'Microsoft YaHei', 'SimSun']
plt.rcParams['axes.unicode_minus'] = False

OUT_DIR = r"F:\26毕设单\基于springboot的飞跃滑雪场管理系统的设计与实现"


def vertical_text(text):
    return "\n".join(text)


# ============================================================
# 1. 系统功能结构图
# ============================================================
def draw_structure():
    fig, ax = plt.subplots(1, 1, figsize=(16, 10), dpi=200)
    ax.set_xlim(-0.5, 16.5)
    ax.set_ylim(-0.5, 10)
    ax.axis('off')

    box_color = '#E8F4FD'
    border_color = '#2196F3'
    root_color = '#BBDEFB'
    role_color = '#C8E6C9'
    func_color = '#FFF9C4'
    font_size = 10

    def draw_box(x, y, w, h, text, color=box_color, fs=font_size, bold=False):
        rect = FancyBboxPatch((x, y), w, h, boxstyle="round,pad=0.05",
                              facecolor=color, edgecolor=border_color, linewidth=1.2)
        ax.add_patch(rect)
        weight = 'bold' if bold else 'normal'
        ax.text(x + w/2, y + h/2, text, ha='center', va='center',
                fontsize=fs, fontweight=weight, linespacing=1.3)

    def draw_line(x1, y1, x2, y2):
        ax.plot([x1, x2], [y1, y2], color=border_color, linewidth=1.2)

    # Root
    draw_box(5.5, 8.8, 5, 0.9, "飞跃滑雪场管理系统", color=root_color, fs=13, bold=True)

    # Role level
    roles = [
        (0.8, 7, 2.5, 0.9, "管理员"),
        (4.3, 7, 2.5, 0.9, "教  练"),
        (7.8, 7, 2.5, 0.9, "工作人员"),
        (11.3, 7, 2.5, 0.9, "普通用户"),
    ]
    for x, y, w, h, name in roles:
        draw_box(x, y, w, h, name, color=role_color, fs=11, bold=True)
        # Line from root to role
        draw_line(8, 8.8, x + w/2, y + h)

    # Functions for each role (vertical text)
    admin_funcs = ["用户\n管理", "教练\n管理", "课程\n管理", "场地\n管理", "商品\n管理", "数据\n统计"]
    coach_funcs = ["我的\n课程", "学员\n预约"]
    staff_funcs = ["商品\n管理", "租赁\n订单", "场地\n信息"]
    user_funcs = ["商品\n租赁", "教练\n预约", "课程\n报名", "场地\n预约", "个人\n中心"]

    all_funcs = [
        (roles[0], admin_funcs),
        (roles[1], coach_funcs),
        (roles[2], staff_funcs),
        (roles[3], user_funcs),
    ]

    for (rx, ry, rw, rh, _), funcs in all_funcs:
        n = len(funcs)
        total_w = n * 1.1
        start_x = rx + rw/2 - total_w/2
        # Vertical connector
        draw_line(rx + rw/2, ry, rx + rw/2, 5.8)
        # Horizontal bar
        if n > 1:
            draw_line(start_x + 0.45, 5.8, start_x + (n-1)*1.1 + 0.45, 5.8)
        for i, func_text in enumerate(funcs):
            fx = start_x + i * 1.1
            fy = 4.2
            draw_box(fx, fy, 0.9, 1.5, func_text, color=func_color, fs=9.5)
            draw_line(fx + 0.45, 5.8, fx + 0.45, fy + 1.5)

    plt.tight_layout()
    path = f"{OUT_DIR}\\structure_chart.png"
    fig.savefig(path, bbox_inches='tight', dpi=200, facecolor='white')
    plt.close()
    print(f"Structure chart saved: {path}")
    return path


# ============================================================
# 2. 用户登录流程图
# ============================================================
def draw_login_flow():
    fig, ax = plt.subplots(1, 1, figsize=(8, 14), dpi=200)
    ax.set_xlim(0, 8)
    ax.set_ylim(0, 14)
    ax.axis('off')

    fs = 10
    box_w, box_h = 3, 0.8
    diamond_s = 0.7

    def draw_rect(cx, cy, text, color='#E3F2FD'):
        x = cx - box_w/2
        y = cy - box_h/2
        rect = FancyBboxPatch((x, y), box_w, box_h, boxstyle="round,pad=0.05",
                              facecolor=color, edgecolor='#1565C0', linewidth=1.2)
        ax.add_patch(rect)
        ax.text(cx, cy, text, ha='center', va='center', fontsize=fs)

    def draw_diamond(cx, cy, text, color='#FFF3E0'):
        s = diamond_s
        diamond = plt.Polygon([(cx, cy+s), (cx+s*1.5, cy), (cx, cy-s), (cx-s*1.5, cy)],
                              facecolor=color, edgecolor='#E65100', linewidth=1.2)
        ax.add_patch(diamond)
        ax.text(cx, cy, text, ha='center', va='center', fontsize=fs-1)

    def draw_oval(cx, cy, text, color='#E8F5E9'):
        oval = patches.Ellipse((cx, cy), box_w*0.7, box_h*0.9,
                               facecolor=color, edgecolor='#2E7D32', linewidth=1.2)
        ax.add_patch(oval)
        ax.text(cx, cy, text, ha='center', va='center', fontsize=fs, fontweight='bold')

    def arrow(x1, y1, x2, y2, label=''):
        ax.annotate('', xy=(x2, y2), xytext=(x1, y1),
                    arrowprops=dict(arrowstyle='->', color='#333', lw=1.2))
        if label:
            mx = (x1+x2)/2
            my = (y1+y2)/2
            ax.text(mx+0.15, my, label, fontsize=fs-2, color='#666')

    cx = 4
    # Start
    draw_oval(cx, 13.2, "开始")
    arrow(cx, 12.75, cx, 12.3)
    
    draw_rect(cx, 11.9, "用户输入用户名和密码")
    arrow(cx, 11.5, cx, 10.8)

    draw_diamond(cx, 10.3, "表单校验？")
    arrow(cx, 9.6, cx, 8.9)
    ax.text(cx+0.15, 9.3, "通过", fontsize=fs-2, color='#666')
    
    # Failed validation
    arrow(cx+diamond_s*1.5, 10.3, 6.5, 10.3)
    draw_rect(6.5, 10.3, "提示错误", color='#FFEBEE')
    ax.text(cx+diamond_s*1.5+0.1, 10.5, "不通过", fontsize=fs-2, color='#666')

    draw_rect(cx, 8.5, "前端发送登录请求到后端")
    arrow(cx, 8.1, cx, 7.4)

    draw_rect(cx, 7, "后端查询数据库验证")
    arrow(cx, 6.6, cx, 5.9)

    draw_diamond(cx, 5.4, "验证通过？")
    arrow(cx, 4.7, cx, 4.1)
    ax.text(cx+0.15, 3.9, "是", fontsize=fs-2, color='#666')

    # Failed auth
    arrow(cx+diamond_s*1.5, 5.4, 6.5, 5.4)
    draw_rect(6.5, 5.4, "返回错误信息", color='#FFEBEE')
    ax.text(cx+diamond_s*1.5+0.1, 5.6, "否", fontsize=fs-2, color='#666')

    draw_rect(cx, 3.7, "生成JWT Token")
    arrow(cx, 3.3, cx, 2.6)

    draw_rect(cx, 2.2, "前端存储Token")
    arrow(cx, 1.8, cx, 1.1)

    draw_rect(cx, 0.7, "根据角色跳转至对应首页")
    arrow(cx, 0.3, cx, -0.2)

    draw_oval(cx, -0.5, "结束")

    plt.tight_layout()
    path = f"{OUT_DIR}\\login_flow.png"
    fig.savefig(path, bbox_inches='tight', dpi=200, facecolor='white')
    plt.close()
    print(f"Login flow saved: {path}")
    return path


# ============================================================
# 3. 商品租赁业务流程图
# ============================================================
def draw_rental_flow():
    fig, ax = plt.subplots(1, 1, figsize=(8, 16), dpi=200)
    ax.set_xlim(0, 8)
    ax.set_ylim(0, 16)
    ax.axis('off')

    fs = 10
    box_w, box_h = 3.2, 0.8
    diamond_s = 0.7

    def draw_rect(cx, cy, text, color='#E3F2FD'):
        x = cx - box_w/2
        y = cy - box_h/2
        rect = FancyBboxPatch((x, y), box_w, box_h, boxstyle="round,pad=0.05",
                              facecolor=color, edgecolor='#1565C0', linewidth=1.2)
        ax.add_patch(rect)
        ax.text(cx, cy, text, ha='center', va='center', fontsize=fs)

    def draw_diamond(cx, cy, text, color='#FFF3E0'):
        s = diamond_s
        diamond = plt.Polygon([(cx, cy+s), (cx+s*1.5, cy), (cx, cy-s), (cx-s*1.5, cy)],
                              facecolor=color, edgecolor='#E65100', linewidth=1.2)
        ax.add_patch(diamond)
        ax.text(cx, cy, text, ha='center', va='center', fontsize=fs-1)

    def draw_oval(cx, cy, text, color='#E8F5E9'):
        oval = patches.Ellipse((cx, cy), box_w*0.7, box_h*0.9,
                               facecolor=color, edgecolor='#2E7D32', linewidth=1.2)
        ax.add_patch(oval)
        ax.text(cx, cy, text, ha='center', va='center', fontsize=fs, fontweight='bold')

    def arrow(x1, y1, x2, y2):
        ax.annotate('', xy=(x2, y2), xytext=(x1, y1),
                    arrowprops=dict(arrowstyle='->', color='#333', lw=1.2))

    cx = 4

    draw_oval(cx, 15.3, "开始")
    arrow(cx, 14.85, cx, 14.4)

    draw_rect(cx, 14, "用户浏览商品列表")
    arrow(cx, 13.6, cx, 12.9)

    draw_rect(cx, 12.5, "选择商品，填写租赁时长和数量")
    arrow(cx, 12.1, cx, 11.4)

    draw_rect(cx, 11, "提交租赁订单")
    arrow(cx, 10.6, cx, 9.9)

    draw_diamond(cx, 9.4, "库存充足？")
    arrow(cx, 8.7, cx, 8.1)
    ax.text(cx+0.15, 7.9, "是", fontsize=fs-2, color='#666')

    arrow(cx+diamond_s*1.5, 9.4, 6.8, 9.4)
    draw_rect(6.8, 9.4, "提示库存\n不足", color='#FFEBEE')
    ax.text(cx+diamond_s*1.5+0.1, 9.6, "否", fontsize=fs-2, color='#666')

    draw_rect(cx, 7.7, "生成订单，扣减库存")
    arrow(cx, 7.3, cx, 6.6)

    draw_rect(cx, 6.2, "用户使用商品")
    arrow(cx, 5.8, cx, 5.1)

    draw_rect(cx, 4.7, "用户归还商品")
    arrow(cx, 4.3, cx, 3.6)

    draw_rect(cx, 3.2, "工作人员确认归还")
    arrow(cx, 2.8, cx, 2.1)

    draw_rect(cx, 1.7, "更新订单状态为\"已归还\"")
    arrow(cx, 1.3, cx, 0.6)

    draw_rect(cx, 0.2, "恢复商品可用库存")
    arrow(cx, -0.2, cx, -0.8)

    draw_oval(cx, -1.1, "结束")

    plt.tight_layout()
    path = f"{OUT_DIR}\\rental_flow.png"
    fig.savefig(path, bbox_inches='tight', dpi=200, facecolor='white')
    plt.close()
    print(f"Rental flow saved: {path}")
    return path


# Generate all
p1 = draw_structure()
p2 = draw_login_flow()
p3 = draw_rental_flow()
print("\nAll diagrams generated!")
