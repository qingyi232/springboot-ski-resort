# -*- coding: utf-8 -*-
import sys
sys.stdout.reconfigure(encoding='utf-8')
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.patches as patches
from matplotlib.patches import FancyBboxPatch
import numpy as np

plt.rcParams['font.sans-serif'] = ['SimHei', 'Microsoft YaHei', 'SimSun']
plt.rcParams['axes.unicode_minus'] = False

OUT_DIR = r"F:\26毕设单\基于springboot的飞跃滑雪场管理系统的设计与实现"


def draw_structure():
    fig, ax = plt.subplots(1, 1, figsize=(18, 9), dpi=200)
    ax.set_xlim(0, 18)
    ax.set_ylim(3, 11)
    ax.axis('off')

    border_color = '#333333'
    root_color = '#BBDEFB'
    role_color = '#C8E6C9'
    func_color = '#FFF9C4'

    def draw_box(x, y, w, h, text, color='#E8F4FD', fs=10, bold=False):
        rect = FancyBboxPatch((x, y), w, h, boxstyle="round,pad=0.05",
                              facecolor=color, edgecolor=border_color, linewidth=1.5)
        ax.add_patch(rect)
        weight = 'bold' if bold else 'normal'
        ax.text(x + w/2, y + h/2, text, ha='center', va='center',
                fontsize=fs, fontweight=weight, linespacing=1.2)

    def draw_line(x1, y1, x2, y2):
        ax.plot([x1, x2], [y1, y2], color=border_color, linewidth=1.2)

    # Root
    root_w, root_h = 5, 0.8
    root_x = 9 - root_w/2
    draw_box(root_x, 9.5, root_w, root_h, "飞跃滑雪场管理系统", color=root_color, fs=13, bold=True)

    # Role level
    role_w, role_h = 2.2, 0.7
    role_y = 7.8
    roles_data = [
        ("管理员", 2.8),
        ("教  练", 6.5),
        ("工作人员", 10.2),
        ("普通用户", 13.9),
    ]

    for name, rx in roles_data:
        draw_box(rx - role_w/2, role_y, role_w, role_h, name, color=role_color, fs=11, bold=True)
        draw_line(9, 9.5, rx, role_y + role_h)

    # Functions
    func_w, func_h = 1.1, 1.2

    funcs_data = {
        2.8: ["用户\n管理", "教练\n管理", "课程\n管理", "场地\n管理", "商品\n管理", "数据\n统计"],
        6.5: ["我的\n课程", "学员\n预约"],
        10.2: ["商品\n管理", "租赁\n订单", "场地\n信息"],
        13.9: ["商品\n租赁", "教练\n预约", "课程\n报名", "场地\n预约", "个人\n中心"],
    }

    func_y_top = 6.8
    func_y = 4.5

    for rx, funcs in funcs_data.items():
        n = len(funcs)
        spacing = 1.3
        total_span = (n - 1) * spacing
        start_x = rx - total_span / 2

        draw_line(rx, role_y, rx, func_y_top)

        if n > 1:
            left_x = start_x
            right_x = start_x + (n - 1) * spacing
            draw_line(left_x, func_y_top, right_x, func_y_top)

        for i, func_text in enumerate(funcs):
            fx = start_x + i * spacing - func_w / 2
            draw_box(fx, func_y, func_w, func_h, func_text, color=func_color, fs=10)
            draw_line(start_x + i * spacing, func_y_top, start_x + i * spacing, func_y + func_h)

    plt.tight_layout(pad=0.5)
    path = f"{OUT_DIR}\\structure_chart.png"
    fig.savefig(path, bbox_inches='tight', dpi=200, facecolor='white')
    plt.close()
    print(f"Structure chart saved")
    return path


def draw_login_flow():
    fig, ax = plt.subplots(1, 1, figsize=(9, 14), dpi=200)
    ax.set_xlim(0, 9)
    ax.set_ylim(-1.5, 14)
    ax.axis('off')

    fs = 11
    box_w, box_h = 3.5, 0.8

    def draw_rect(cx, cy, text, color='#E3F2FD'):
        x, y = cx - box_w/2, cy - box_h/2
        rect = FancyBboxPatch((x, y), box_w, box_h, boxstyle="round,pad=0.05",
                              facecolor=color, edgecolor='#333', linewidth=1.3)
        ax.add_patch(rect)
        ax.text(cx, cy, text, ha='center', va='center', fontsize=fs)

    def draw_diamond(cx, cy, text, color='#FFF3E0'):
        s = 0.7
        pts = [(cx, cy+s), (cx+s*1.6, cy), (cx, cy-s), (cx-s*1.6, cy)]
        diamond = plt.Polygon(pts, facecolor=color, edgecolor='#333', linewidth=1.3)
        ax.add_patch(diamond)
        ax.text(cx, cy, text, ha='center', va='center', fontsize=fs-1)

    def draw_oval(cx, cy, text, color='#E8F5E9'):
        oval = patches.Ellipse((cx, cy), 2.2, 0.7,
                               facecolor=color, edgecolor='#333', linewidth=1.3)
        ax.add_patch(oval)
        ax.text(cx, cy, text, ha='center', va='center', fontsize=fs, fontweight='bold')

    def arrow(x1, y1, x2, y2):
        ax.annotate('', xy=(x2, y2), xytext=(x1, y1),
                    arrowprops=dict(arrowstyle='->', color='#333', lw=1.3))

    cx = 4
    gap = 1.5

    y = 13; draw_oval(cx, y, "开始")
    arrow(cx, y-0.35, cx, y-0.8)

    y -= gap; draw_rect(cx, y, "用户输入用户名和密码")
    arrow(cx, y-box_h/2, cx, y-box_h/2-0.5)

    y -= gap; draw_diamond(cx, y, "表单校验?")
    ax.text(cx+0.15, y-0.9, "通过", fontsize=fs-2, color='#555')
    ax.text(cx+1.3, y+0.2, "不通过", fontsize=fs-2, color='#555')
    arrow(cx+0.7*1.6, y, 7, y)
    draw_rect(7, y, "提示错误", color='#FFCDD2')
    arrow(cx, y-0.7, cx, y-0.7-0.3)

    y -= gap; draw_rect(cx, y, "前端发送登录请求到后端")
    arrow(cx, y-box_h/2, cx, y-box_h/2-0.5)

    y -= gap; draw_rect(cx, y, "后端查询数据库验证")
    arrow(cx, y-box_h/2, cx, y-box_h/2-0.5)

    y -= gap; draw_diamond(cx, y, "验证通过?")
    ax.text(cx+0.15, y-0.9, "是", fontsize=fs-2, color='#555')
    ax.text(cx+1.3, y+0.2, "否", fontsize=fs-2, color='#555')
    arrow(cx+0.7*1.6, y, 7, y)
    draw_rect(7, y, "返回错误信息", color='#FFCDD2')
    arrow(cx, y-0.7, cx, y-0.7-0.3)

    y -= gap; draw_rect(cx, y, "生成JWT Token返回前端")
    arrow(cx, y-box_h/2, cx, y-box_h/2-0.5)

    y -= gap; draw_rect(cx, y, "前端存储Token到本地")
    arrow(cx, y-box_h/2, cx, y-box_h/2-0.5)

    y -= gap; draw_rect(cx, y, "根据角色跳转至对应首页")
    arrow(cx, y-box_h/2, cx, y-box_h/2-0.5)

    y -= gap*0.7; draw_oval(cx, y, "结束")

    plt.tight_layout()
    path = f"{OUT_DIR}\\login_flow.png"
    fig.savefig(path, bbox_inches='tight', dpi=200, facecolor='white')
    plt.close()
    print(f"Login flow saved")
    return path


def draw_rental_flow():
    fig, ax = plt.subplots(1, 1, figsize=(9, 16), dpi=200)
    ax.set_xlim(0, 9)
    ax.set_ylim(-2, 16)
    ax.axis('off')

    fs = 11
    box_w, box_h = 3.5, 0.8

    def draw_rect(cx, cy, text, color='#E3F2FD'):
        x, y = cx - box_w/2, cy - box_h/2
        rect = FancyBboxPatch((x, y), box_w, box_h, boxstyle="round,pad=0.05",
                              facecolor=color, edgecolor='#333', linewidth=1.3)
        ax.add_patch(rect)
        ax.text(cx, cy, text, ha='center', va='center', fontsize=fs)

    def draw_diamond(cx, cy, text, color='#FFF3E0'):
        s = 0.7
        pts = [(cx, cy+s), (cx+s*1.6, cy), (cx, cy-s), (cx-s*1.6, cy)]
        diamond = plt.Polygon(pts, facecolor=color, edgecolor='#333', linewidth=1.3)
        ax.add_patch(diamond)
        ax.text(cx, cy, text, ha='center', va='center', fontsize=fs-1)

    def draw_oval(cx, cy, text, color='#E8F5E9'):
        oval = patches.Ellipse((cx, cy), 2.2, 0.7,
                               facecolor=color, edgecolor='#333', linewidth=1.3)
        ax.add_patch(oval)
        ax.text(cx, cy, text, ha='center', va='center', fontsize=fs, fontweight='bold')

    def arrow(x1, y1, x2, y2):
        ax.annotate('', xy=(x2, y2), xytext=(x1, y1),
                    arrowprops=dict(arrowstyle='->', color='#333', lw=1.3))

    cx = 4
    gap = 1.4

    y = 15; draw_oval(cx, y, "开始")
    arrow(cx, y-0.35, cx, y-0.75)

    y -= gap; draw_rect(cx, y, "用户浏览商品列表")
    arrow(cx, y-box_h/2, cx, y-box_h/2-0.4)

    y -= gap; draw_rect(cx, y, "选择商品，填写租赁时长和数量")
    arrow(cx, y-box_h/2, cx, y-box_h/2-0.4)

    y -= gap; draw_rect(cx, y, "提交租赁订单")
    arrow(cx, y-box_h/2, cx, y-box_h/2-0.4)

    y -= gap; draw_diamond(cx, y, "库存充足?")
    ax.text(cx+0.15, y-0.9, "是", fontsize=fs-2, color='#555')
    ax.text(cx+1.3, y+0.2, "否", fontsize=fs-2, color='#555')
    arrow(cx+0.7*1.6, y, 7.2, y)
    draw_rect(7.2, y, "提示库存不足", color='#FFCDD2')
    arrow(cx, y-0.7, cx, y-0.7-0.3)

    y -= gap; draw_rect(cx, y, "生成订单，扣减库存")
    arrow(cx, y-box_h/2, cx, y-box_h/2-0.4)

    y -= gap; draw_rect(cx, y, "用户使用租赁商品")
    arrow(cx, y-box_h/2, cx, y-box_h/2-0.4)

    y -= gap; draw_rect(cx, y, "用户归还商品")
    arrow(cx, y-box_h/2, cx, y-box_h/2-0.4)

    y -= gap; draw_rect(cx, y, "工作人员确认归还操作")
    arrow(cx, y-box_h/2, cx, y-box_h/2-0.4)

    y -= gap; draw_rect(cx, y, "订单状态更新为\"已归还\"")
    arrow(cx, y-box_h/2, cx, y-box_h/2-0.4)

    y -= gap; draw_rect(cx, y, "恢复商品可用库存数量")
    arrow(cx, y-box_h/2, cx, y-box_h/2-0.4)

    y -= gap*0.7; draw_oval(cx, y, "结束")

    plt.tight_layout()
    path = f"{OUT_DIR}\\rental_flow.png"
    fig.savefig(path, bbox_inches='tight', dpi=200, facecolor='white')
    plt.close()
    print(f"Rental flow saved")
    return path


draw_structure()
draw_login_flow()
draw_rental_flow()
print("All done!")
