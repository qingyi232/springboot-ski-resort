# -*- coding: utf-8 -*-
import sys
sys.stdout.reconfigure(encoding='utf-8')
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
from matplotlib.patches import FancyBboxPatch
import numpy as np

plt.rcParams['font.sans-serif'] = ['SimHei', 'Microsoft YaHei']
plt.rcParams['axes.unicode_minus'] = False

OUT_DIR = r"F:\26毕设单\基于springboot的飞跃滑雪场管理系统的设计与实现"


def vert(text):
    """Convert text to vertical layout (one char per line)"""
    return "\n".join(text)


fig, ax = plt.subplots(1, 1, figsize=(20, 10), dpi=200)
ax.set_xlim(0, 20)
ax.set_ylim(2.5, 11.5)
ax.axis('off')

border = '#333333'

def box(x, y, w, h, text, color, fs=10, bold=False, vertical=False):
    rect = FancyBboxPatch((x-w/2, y-h/2), w, h, boxstyle="round,pad=0.06",
                          facecolor=color, edgecolor=border, linewidth=1.3)
    ax.add_patch(rect)
    t = vert(text) if vertical else text
    ax.text(x, y, t, ha='center', va='center',
            fontsize=fs, fontweight='bold' if bold else 'normal',
            linespacing=1.1)

def line(x1, y1, x2, y2):
    ax.plot([x1, x2], [y1, y2], color=border, linewidth=1.2, solid_capstyle='round')

# ===== ROOT =====
box(10, 10.5, 6, 0.8, "飞跃滑雪场管理系统", '#BBDEFB', fs=14, bold=True)

# ===== ROLE LEVEL =====
role_y = 8.8
roles = [
    (3, "管理员"),
    (8, "教  练"),
    (12.5, "工作人员"),
    (17, "普通用户"),
]
for rx, rname in roles:
    box(rx, role_y, 2.2, 0.7, rname, '#C8E6C9', fs=12, bold=True)
    line(10, 10.1, rx, role_y + 0.35)

# ===== FUNCTION LEVEL =====
# Vertical boxes, one char per line, taller and narrower
func_top = 7.5
func_bot = 4.2
func_h = func_top - func_bot  # 3.3 height
func_w = 0.75

funcs_by_role = {
    3: ["用户管理", "教练管理", "课程管理", "场地管理", "商品管理", "数据统计"],
    8: ["我的课程", "学员预约"],
    12.5: ["商品管理", "租赁订单", "场地信息"],
    17: ["商品租赁", "教练预约", "课程报名", "场地预约", "个人中心"],
}

spacing = 1.0

for rx, funcs in funcs_by_role.items():
    n = len(funcs)
    total_span = (n - 1) * spacing
    start = rx - total_span / 2

    # Vertical connector from role to horizontal bar
    line(rx, role_y - 0.35, rx, func_top + 0.3)

    # Horizontal bar
    if n > 1:
        left = start
        right = start + (n-1) * spacing
        line(left, func_top + 0.3, right, func_top + 0.3)

    for i, fname in enumerate(funcs):
        fx = start + i * spacing
        # Vertical connector from bar to box
        line(fx, func_top + 0.3, fx, func_top)

        # Determine box height based on text length
        nchars = len(fname)
        bh = max(nchars * 0.55 + 0.3, 2.0)
        by = func_top - bh / 2

        box(fx, by, func_w, bh, fname, '#FFF9C4', fs=10.5, vertical=True)

plt.tight_layout(pad=0.3)
path = f"{OUT_DIR}\\structure_chart.png"
fig.savefig(path, bbox_inches='tight', dpi=200, facecolor='white')
plt.close()
print(f"Structure chart saved to {path}")
