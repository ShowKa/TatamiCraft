"""
Generate 16x16 pixel-art textures for 4 sliding door variants.
Outputs PNG files to common-resources/assets/tatamicraft/textures/
"""

from pathlib import Path
from PIL import Image

OUT_BLOCK = Path(__file__).parent.parent / "common-resources/assets/tatamicraft/textures/block"
OUT_ITEM  = Path(__file__).parent.parent / "common-resources/assets/tatamicraft/textures/item"
OUT_BLOCK.mkdir(parents=True, exist_ok=True)
OUT_ITEM.mkdir(parents=True, exist_ok=True)

def px(r,g,b,a=255): return (r,g,b,a)

# ──────────────────────────────────────────────────────────────────────────────
# Palette definitions
# ──────────────────────────────────────────────────────────────────────────────
TRANSPARENT = px(0,0,0,0)

# Shared wood frame colour (thin edge) - reuse fusuma_edge style
WOOD_DARK  = px( 80, 55, 30)
WOOD_MID   = px(110, 76, 40)
WOOD_LIGHT = px(140, 98, 55)

# ── Shoji ──────────────────────────────────────────────────────────────────────
SH_BG      = px(242, 238, 225)   # off-white washi paper
SH_GRID    = px(180, 165, 130)   # light grid lines
SH_FRAME   = px(110, 80, 40)     # dark wood frame
SH_HANDLE  = px( 80, 55, 30)     # handle

# ── Sliding Window ─────────────────────────────────────────────────────────────
SW_GLASS   = px(180, 215, 230)   # light blue glass
SW_FRAME   = px(100, 75, 45)     # wood frame
SW_HANDLE  = px( 70, 50, 28)

# ── Wooden Sliding Door ────────────────────────────────────────────────────────
WD_LIGHT   = px(178, 130, 75)    # light wood grain
WD_MID     = px(152, 108, 58)
WD_DARK    = px(128,  88, 44)    # dark grain
WD_FRAME   = px( 90,  62, 30)
WD_HANDLE  = px( 65,  42, 20)

# ── Frosted Glass Sliding Door ─────────────────────────────────────────────────
FG_GLASS   = px(230, 235, 238)   # milky white frosted glass
FG_TINT    = px(210, 218, 224)   # slightly darker tint for depth
FG_FRAME   = px(105, 78, 48)     # wood frame
FG_HANDLE  = px( 75, 55, 32)

# ──────────────────────────────────────────────────────────────────────────────
# Generic save helper
# ──────────────────────────────────────────────────────────────────────────────
def save(name: str, pixels: list[list], folder: Path):
    assert len(pixels) == 16
    assert all(len(row) == 16 for row in pixels)
    img = Image.new("RGBA", (16, 16))
    for y, row in enumerate(pixels):
        for x, col in enumerate(row):
            img.putpixel((x, y), col)
    out = folder / name
    img.save(out)
    print(f"  wrote {out.relative_to(out.parent.parent.parent.parent.parent)}")

# ──────────────────────────────────────────────────────────────────────────────
# Shoji textures
# ──────────────────────────────────────────────────────────────────────────────
def make_shoji_corner():
    """Corner tile: frame on top & left edges, washi paper fill with subtle grid."""
    g = SH_GRID
    b = SH_BG
    f = SH_FRAME
    rows = []
    for y in range(16):
        row = []
        for x in range(16):
            if x == 0 or x == 1 or y == 0 or y == 1:
                row.append(f)
            elif x % 4 == 2 or y % 4 == 2:
                row.append(g)
            else:
                row.append(b)
        rows.append(row)
    return rows

def make_shoji_edge():
    """Edge tile: frame on left, handle on outer-left edge, washi paper fill."""
    g = SH_GRID; b = SH_BG; f = SH_FRAME; h = SH_HANDLE
    rows = []
    for y in range(16):
        row = []
        for x in range(16):
            if x == 0 or x == 1:
                row.append(f)
            elif x % 4 == 2 or y % 4 == 2:
                row.append(g)
            else:
                row.append(b)
        rows.append(row)
    # handle at x=3-4 (left side of texture) so it appears on outer edge of each panel
    for yh in range(6, 10):
        for xh in range(3, 5):
            rows[yh][xh] = h
    return rows

def make_shoji_edge_plain():
    """Edge tile without handle."""
    g = SH_GRID; b = SH_BG; f = SH_FRAME
    rows = []
    for y in range(16):
        row = []
        for x in range(16):
            if x == 0 or x == 1:
                row.append(f)
            elif x % 4 == 2 or y % 4 == 2:
                row.append(g)
            else:
                row.append(b)
        rows.append(row)
    return rows

# ──────────────────────────────────────────────────────────────────────────────
# Sliding Window textures
# ──────────────────────────────────────────────────────────────────────────────
def make_sliding_window_corner():
    """Corner tile: wood frame on top & left, glass fill."""
    glass = SW_GLASS; fr = SW_FRAME
    rows = []
    for y in range(16):
        row = []
        for x in range(16):
            if x <= 1 or y <= 1:
                row.append(fr)
            else:
                # subtle glass highlight
                if (x + y) % 8 == 0:
                    row.append(px(glass[0]+10, glass[1]+10, glass[2]+10))
                else:
                    row.append(glass)
        rows.append(row)
    return rows

def make_sliding_window_edge():
    """Edge tile: wood frame on left, glass fill, handle."""
    glass = SW_GLASS; fr = SW_FRAME; h = SW_HANDLE
    rows = []
    for y in range(16):
        row = []
        for x in range(16):
            if x <= 1:
                row.append(fr)
            elif (x + y) % 8 == 0:
                row.append(px(glass[0]+10, glass[1]+10, glass[2]+10))
            else:
                row.append(glass)
        rows.append(row)
    for yh in range(6, 10):
        for xh in range(13, 15):
            rows[yh][xh] = h
    return rows

def make_sliding_window_edge_plain():
    """Edge tile without handle."""
    glass = SW_GLASS; fr = SW_FRAME
    rows = []
    for y in range(16):
        row = []
        for x in range(16):
            if x <= 1:
                row.append(fr)
            elif (x + y) % 8 == 0:
                row.append(px(glass[0]+10, glass[1]+10, glass[2]+10))
            else:
                row.append(glass)
        rows.append(row)
    return rows

# ──────────────────────────────────────────────────────────────────────────────
# Wooden Sliding Door textures
# ──────────────────────────────────────────────────────────────────────────────
WOOD_GRAIN = [
    WD_LIGHT, WD_MID, WD_DARK, WD_LIGHT, WD_MID, WD_MID, WD_DARK, WD_LIGHT,
    WD_MID, WD_DARK, WD_LIGHT, WD_MID, WD_LIGHT, WD_DARK, WD_MID, WD_LIGHT,
]

def make_wooden_corner():
    """Corner: frame top+left, wood grain fill."""
    fr = WD_FRAME
    rows = []
    for y in range(16):
        row = []
        for x in range(16):
            if x <= 1 or y <= 1:
                row.append(fr)
            else:
                row.append(WOOD_GRAIN[x % 16])
        rows.append(row)
    return rows

def make_wooden_edge():
    """Edge: frame left, wood grain fill, handle."""
    fr = WD_FRAME; h = WD_HANDLE
    rows = []
    for y in range(16):
        row = []
        for x in range(16):
            if x <= 1:
                row.append(fr)
            else:
                row.append(WOOD_GRAIN[x % 16])
        rows.append(row)
    for yh in range(6, 10):
        for xh in range(13, 15):
            rows[yh][xh] = h
    return rows

def make_wooden_edge_plain():
    """Edge: frame left, wood grain fill, no handle."""
    fr = WD_FRAME
    rows = []
    for y in range(16):
        row = []
        for x in range(16):
            if x <= 1:
                row.append(fr)
            else:
                row.append(WOOD_GRAIN[x % 16])
        rows.append(row)
    return rows

# ──────────────────────────────────────────────────────────────────────────────
# Frosted Glass Sliding Door textures
# ──────────────────────────────────────────────────────────────────────────────
def make_frosted_corner():
    """Corner: wood frame top+left, milky frosted glass fill."""
    glass = FG_GLASS; tint = FG_TINT; fr = FG_FRAME
    rows = []
    for y in range(16):
        row = []
        for x in range(16):
            if x <= 1 or y <= 1:
                row.append(fr)
            elif (x * 3 + y * 2) % 7 == 0:
                row.append(tint)
            else:
                row.append(glass)
        rows.append(row)
    return rows

def make_frosted_edge():
    """Edge: wood frame left, milky glass fill, handle."""
    glass = FG_GLASS; tint = FG_TINT; fr = FG_FRAME; h = FG_HANDLE
    rows = []
    for y in range(16):
        row = []
        for x in range(16):
            if x <= 1:
                row.append(fr)
            elif (x * 3 + y * 2) % 7 == 0:
                row.append(tint)
            else:
                row.append(glass)
        rows.append(row)
    for yh in range(6, 10):
        for xh in range(13, 15):
            rows[yh][xh] = h
    return rows

def make_frosted_edge_plain():
    """Edge: wood frame left, milky glass fill, no handle."""
    glass = FG_GLASS; tint = FG_TINT; fr = FG_FRAME
    rows = []
    for y in range(16):
        row = []
        for x in range(16):
            if x <= 1:
                row.append(fr)
            elif (x * 3 + y * 2) % 7 == 0:
                row.append(tint)
            else:
                row.append(glass)
        rows.append(row)
    return rows

# ──────────────────────────────────────────────────────────────────────────────
# Item icons (small 16x16 representation of the full door face)
# ──────────────────────────────────────────────────────────────────────────────
def make_item_icon(corner_pixels):
    """Use the corner texture as item icon (representative face view)."""
    return [row[:] for row in corner_pixels]

# ──────────────────────────────────────────────────────────────────────────────
# Generate all textures
# ──────────────────────────────────────────────────────────────────────────────
print("Generating sliding door textures...")

# Shoji
sh_c = make_shoji_corner()
save("shoji_tile_corner.png",     sh_c,               OUT_BLOCK)
save("shoji_tile_edge.png",       make_shoji_edge(),  OUT_BLOCK)
save("shoji_tile_edge_plain.png", make_shoji_edge_plain(), OUT_BLOCK)
save("shoji_item.png",            make_item_icon(sh_c), OUT_ITEM)

# Sliding Window
sw_c = make_sliding_window_corner()
save("sliding_window_tile_corner.png",     sw_c,                            OUT_BLOCK)
save("sliding_window_tile_edge.png",       make_sliding_window_edge(),      OUT_BLOCK)
save("sliding_window_tile_edge_plain.png", make_sliding_window_edge_plain(), OUT_BLOCK)
save("sliding_window_item.png",            make_item_icon(sw_c),             OUT_ITEM)

# Wooden Sliding Door
wd_c = make_wooden_corner()
save("wooden_sliding_door_tile_corner.png",     wd_c,                   OUT_BLOCK)
save("wooden_sliding_door_tile_edge.png",       make_wooden_edge(),     OUT_BLOCK)
save("wooden_sliding_door_tile_edge_plain.png", make_wooden_edge_plain(), OUT_BLOCK)
save("wooden_sliding_door_item.png",            make_item_icon(wd_c),   OUT_ITEM)

# Frosted Glass Sliding Door
fg_c = make_frosted_corner()
save("frosted_glass_sliding_door_tile_corner.png",     fg_c,                    OUT_BLOCK)
save("frosted_glass_sliding_door_tile_edge.png",       make_frosted_edge(),     OUT_BLOCK)
save("frosted_glass_sliding_door_tile_edge_plain.png", make_frosted_edge_plain(), OUT_BLOCK)
save("frosted_glass_sliding_door_item.png",            make_item_icon(fg_c),    OUT_ITEM)

print("Done! 16 textures generated.")
