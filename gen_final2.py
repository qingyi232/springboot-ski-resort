# -*- coding: utf-8 -*-
import sys
sys.stdout.reconfigure(encoding='utf-8')
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
from matplotlib.patches import Rectangle, Ellipse, FancyArrowPatch
from matplotlib.font_manager import FontProperties

plt.rcParams['axes.unicode_minus'] = False
FONT = r'C:\Windows\Fonts\simsun.ttc'
OUT = r"F:\26毕设单\基于springboot的飞跃滑雪场管理系统的设计与实现"

def mkfont(sz):
    return FontProperties(fname=FONT, size=sz)


# ============================================================
# 1. 功能结构图 - 大字体竖排
# ============================================================
def structure():
    fig, ax = plt.subplots(figsize=(20, 12), dpi=150)
    ax.set_xlim(0, 20)
    ax.set_ylim(1.5, 12)
    ax.axis('off')
    fig.patch.set_facecolor('white')

    def box(x, y, w, h, text, fs=12, vert=False):
        ax.add_patch(Rectangle((x-w/2, y-h/2), w, h, fc='white', ec='black', lw=1.5))
        t = "\n".join(text) if vert else text
        ax.text(x, y, t, ha='center', va='center', fontproperties=mkfont(fs), linespacing=1.1)

    def ln(x1, y1, x2, y2):
        ax.plot([x1, x2], [y1, y2], 'k-', lw=1.3)

    # Root
    box(10, 11, 6, 1, "飞跃滑雪场管理系统", fs=16)

    # Roles
    ry = 9
    rw, rh = 2.5, 0.8
    roles = [(3, "管理员"), (8, "教  练"), (12.5, "工作人员"), (17, "普通用户")]
    for rx, rn in roles:
        box(rx, ry, rw, rh, rn, fs=14)
        ln(10, 10.5, rx, ry + rh/2)

    # Functions
    bar_y = 7.5
    fw, fh_base = 0.85, 0  # height calculated per text
    sp = 1.15  # spacing between boxes

    funcs = {
        3: ["用户管理", "教练管理", "课程管理", "场地管理", "商品管理", "数据统计"],
        8: ["我的课程", "学员预约"],
        12.5: ["商品管理", "租赁订单", "场地信息"],
        17: ["商品租赁", "教练预约", "课程报名", "场地预约", "个人中心"],
    }

    for rx, flist in funcs.items():
        n = len(flist)
        total = (n-1) * sp
        start = rx - total/2

        ln(rx, ry - rh/2, rx, bar_y)
        if n > 1:
            ln(start, bar_y, start + (n-1)*sp, bar_y)

        for i, fname in enumerate(flist):
            fx = start + i * sp
            ln(fx, bar_y, fx, bar_y - 0.2)
            nch = len(fname)
            fh = nch * 0.65 + 0.4
            by = bar_y - 0.2 - fh/2
            box(fx, by, fw, fh, fname, fs=13, vert=True)

    fig.savefig(f"{OUT}\\structure_chart.png", bbox_inches='tight', dpi=150, facecolor='white')
    plt.close()
    print("Structure done")


# ============================================================
# 2. 登录流程图 - 修正连线
# ============================================================
def login_flow():
    fig, ax = plt.subplots(figsize=(10, 16), dpi=150)
    ax.set_xlim(-0.5, 10)
    ax.set_ylim(-1, 16)
    ax.axis('off')
    fig.patch.set_facecolor('white')

    fs = 12
    f = mkfont(fs)
    f_s = mkfont(fs-2)
    bw, bh = 4, 0.85

    def rect(cx, cy, text, w=bw):
        ax.add_patch(Rectangle((cx-w/2, cy-bh/2), w, bh, fc='white', ec='black', lw=1.3))
        ax.text(cx, cy, text, ha='center', va='center', fontproperties=f)

    def diam(cx, cy, text):
        s = 0.75
        pts = [(cx, cy+s), (cx+s*1.4, cy), (cx, cy-s), (cx-s*1.4, cy)]
        ax.add_patch(plt.Polygon(pts, fc='white', ec='black', lw=1.3))
        ax.text(cx, cy, text, ha='center', va='center', fontproperties=f)

    def oval(cx, cy, text):
        ax.add_patch(Ellipse((cx, cy), 2.2, 0.8, fc='white', ec='black', lw=1.3))
        ax.text(cx, cy, text, ha='center', va='center', fontproperties=f)

    def arr(x1, y1, x2, y2):
        ax.annotate('', xy=(x2, y2), xytext=(x1, y1),
                    arrowprops=dict(arrowstyle='->', color='black', lw=1.3))

    def lbl(x, y, text):
        ax.text(x, y, text, fontproperties=f_s)

    cx = 4
    g = 1.7
    err_x = 7.5
    err_w = 2.5

    y = 15; oval(cx, y, "开始")
    arr(cx, y-0.4, cx, y-0.9)

    y -= g; rect(cx, y, "用户输入用户名和密码")
    arr(cx, y-bh/2, cx, y-bh/2-0.55)

    y -= g; diam(cx, y, "表单校验")
    lbl(cx+0.1, y-0.95, "通过")
    lbl(cx+1.2, y+0.25, "不通过")
    # Right branch - error box
    arr(cx+0.75*1.4, y, err_x-err_w/2, y)
    rect(err_x, y, "提示错误", w=err_w)
    arr(cx, y-0.75, cx, y-0.75-0.5)

    y -= g; rect(cx, y, "发送登录请求到后端")
    arr(cx, y-bh/2, cx, y-bh/2-0.55)

    y -= g; rect(cx, y, "后端查询数据库验证")
    arr(cx, y-bh/2, cx, y-bh/2-0.55)

    y -= g; diam(cx, y, "验证通过")
    lbl(cx+0.1, y-0.95, "是")
    lbl(cx+1.2, y+0.25, "否")
    arr(cx+0.75*1.4, y, err_x-err_w/2, y)
    rect(err_x, y, "返回错误信息", w=err_w)
    arr(cx, y-0.75, cx, y-0.75-0.5)

    y -= g; rect(cx, y, "生成JWT Token")
    arr(cx, y-bh/2, cx, y-bh/2-0.55)

    y -= g; rect(cx, y, "前端存储Token")
    arr(cx, y-bh/2, cx, y-bh/2-0.55)

    y -= g; rect(cx, y, "根据角色跳转对应首页")
    arr(cx, y-bh/2, cx, y-bh/2-0.5)

    y -= g*0.7; oval(cx, y, "结束")

    fig.savefig(f"{OUT}\\login_flow.png", bbox_inches='tight', dpi=150, facecolor='white')
    plt.close()
    print("Login flow done")


# ============================================================
# 3. 租赁流程图 - 修正连线
# ============================================================
def rental_flow():
    fig, ax = plt.subplots(figsize=(10, 18), dpi=150)
    ax.set_xlim(-0.5, 10)
    ax.set_ylim(-2, 17)
    ax.axis('off')
    fig.patch.set_facecolor('white')

    fs = 12
    f = mkfont(fs)
    f_s = mkfont(fs-2)
    bw, bh = 4, 0.85

    def rect(cx, cy, text, w=bw):
        ax.add_patch(Rectangle((cx-w/2, cy-bh/2), w, bh, fc='white', ec='black', lw=1.3))
        ax.text(cx, cy, text, ha='center', va='center', fontproperties=f)

    def diam(cx, cy, text):
        s = 0.75
        pts = [(cx, cy+s), (cx+s*1.4, cy), (cx, cy-s), (cx-s*1.4, cy)]
        ax.add_patch(plt.Polygon(pts, fc='white', ec='black', lw=1.3))
        ax.text(cx, cy, text, ha='center', va='center', fontproperties=f)

    def oval(cx, cy, text):
        ax.add_patch(Ellipse((cx, cy), 2.2, 0.8, fc='white', ec='black', lw=1.3))
        ax.text(cx, cy, text, ha='center', va='center', fontproperties=f)

    def arr(x1, y1, x2, y2):
        ax.annotate('', xy=(x2, y2), xytext=(x1, y1),
                    arrowprops=dict(arrowstyle='->', color='black', lw=1.3))

    def lbl(x, y, text):
        ax.text(x, y, text, fontproperties=f_s)

    cx = 4
    g = 1.5
    err_x = 7.5
    err_w = 2.5

    y = 16; oval(cx, y, "开始")
    arr(cx, y-0.4, cx, y-0.85)

    y -= g; rect(cx, y, "用户浏览商品列表")
    arr(cx, y-bh/2, cx, y-bh/2-0.4)

    y -= g; rect(cx, y, "选择商品，填写租赁信息")
    arr(cx, y-bh/2, cx, y-bh/2-0.4)

    y -= g; rect(cx, y, "提交租赁订单")
    arr(cx, y-bh/2, cx, y-bh/2-0.4)

    y -= g; diam(cx, y, "库存充足")
    lbl(cx+0.1, y-0.95, "是")
    lbl(cx+1.2, y+0.25, "否")
    arr(cx+0.75*1.4, y, err_x-err_w/2, y)
    rect(err_x, y, "提示库存不足", w=err_w)
    arr(cx, y-0.75, cx, y-0.75-0.4)

    y -= g; rect(cx, y, "生成订单，扣减库存")
    arr(cx, y-bh/2, cx, y-bh/2-0.4)

    y -= g; rect(cx, y, "用户使用租赁商品")
    arr(cx, y-bh/2, cx, y-bh/2-0.4)

    y -= g; rect(cx, y, "用户归还商品")
    arr(cx, y-bh/2, cx, y-bh/2-0.4)

    y -= g; rect(cx, y, "工作人员确认归还")
    arr(cx, y-bh/2, cx, y-bh/2-0.4)

    y -= g; rect(cx, y, "更新订单状态为已归还")
    arr(cx, y-bh/2, cx, y-bh/2-0.4)

    y -= g; rect(cx, y, "恢复商品可用库存")
    arr(cx, y-bh/2, cx, y-bh/2-0.4)

    y -= g*0.6; oval(cx, y, "结束")

    fig.savefig(f"{OUT}\\rental_flow.png", bbox_inches='tight', dpi=150, facecolor='white')
    plt.close()
    print("Rental flow done")


structure()
login_flow()
rental_flow()
print("All done!")
