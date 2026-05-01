#!/usr/bin/env python3
"""
Generate color-variant Fusuma textures referencing tatami block PNGs.

From each tatami_block_{color}.png, extracts two representative colors:
  - dark endpoint  : average of the darkest 25% of panel pixels
  - light endpoint : average of the lightest 25% of panel pixels

Each fusuma panel pixel (luminance >= 90) is then remapped to a linear
interpolation between those two endpoints at the pixel's own relative
luminance. Frame pixels (luminance < 90) are left unchanged.

Using only 2 averaged endpoints eliminates per-pixel tatami noise entirely;
the smoothness of the output depends solely on the fusuma source texture.
"""

import sys
from pathlib import Path

try:
    from PIL import Image
except ImportError:
    print("Pillow not installed. Run: pip install Pillow")
    sys.exit(1)

REPO_ROOT = Path(__file__).parent.parent
TEXTURES = REPO_ROOT / "common-resources/assets/tatamicraft/textures"

BLOCK_SOURCES = [
    "block/fusuma_tile_corner.png",
    "block/fusuma_tile_edge.png",
    "block/fusuma_tile_edge_plain.png",
]
ITEM_SOURCE = "item/fusuma_item.png"

VARIANTS = [
    "grayish_pink",
    "milk_white",
    "light_brown",
    "walnut",
]

def lum(r: int, g: int, b: int) -> float:
    return 0.299 * r + 0.587 * g + 0.114 * b


def avg(colors: list) -> tuple:
    n = len(colors)
    return (sum(c[0] for c in colors) // n,
            sum(c[1] for c in colors) // n,
            sum(c[2] for c in colors) // n)


def extract_endpoints(tatami_path: Path) -> tuple:
    """Return (dark_color, light_color) from all opaque tatami pixels."""
    img = Image.open(tatami_path).convert("RGBA")
    all_px = sorted(
        [(r, g, b) for r, g, b, a in img.getdata() if a > 128],
        key=lambda c: lum(*c)
    )
    if not all_px:
        return (40, 30, 20), (255, 255, 255)

    quarter = max(1, len(all_px) // 4)
    return avg(all_px[:quarter]), avg(all_px[-quarter:])


def remap(base: Image.Image, dark: tuple, light: tuple) -> Image.Image:
    """Linearly remap ALL opaque pixels (frame + panel) between dark and light."""
    base_rgba = base.convert("RGBA")
    result = base_rgba.copy()
    out_px = result.load()
    in_px = base_rgba.load()
    w, h = base_rgba.size

    all_lums = [lum(r, g, b)
                for y in range(h) for x in range(w)
                for r, g, b, a in [in_px[x, y]]
                if a > 128]
    if not all_lums:
        return result

    lo, hi = min(all_lums), max(all_lums)
    rng = hi - lo if hi > lo else 1

    for y in range(h):
        for x in range(w):
            r, g, b, a = in_px[x, y]
            if a > 128:
                t = (lum(r, g, b) - lo) / rng
                out_px[x, y] = (
                    int(dark[0] + (light[0] - dark[0]) * t),
                    int(dark[1] + (light[1] - dark[1]) * t),
                    int(dark[2] + (light[2] - dark[2]) * t),
                    a,
                )
    return result


def generate():
    for color_id in VARIANTS:
        tatami_path = TEXTURES / f"block/tatami_block_{color_id}.png"
        if not tatami_path.exists():
            print(f"WARNING: tatami reference not found: {tatami_path}")
            continue

        dark, light = extract_endpoints(tatami_path)
        print(f"{color_id}: dark={dark}  light={light}")
        suffix = f"_{color_id}"

        for src_path in BLOCK_SOURCES:
            src = TEXTURES / src_path
            if not src.exists():
                print(f"WARNING: source not found: {src}")
                continue
            out = remap(Image.open(src), dark, light)
            out_path = TEXTURES / src_path.replace(".png", f"{suffix}.png")
            out.save(out_path)
            print(f"  {Path(out_path).relative_to(REPO_ROOT)}")

        src = TEXTURES / ITEM_SOURCE
        if src.exists():
            out = remap(Image.open(src), dark, light)
            out_path = TEXTURES / f"item/fusuma_item{suffix}.png"
            out.save(out_path)
            print(f"  {Path(out_path).relative_to(REPO_ROOT)}")
        else:
            print(f"WARNING: item source not found: {src}")

    print("Done.")


if __name__ == "__main__":
    generate()
