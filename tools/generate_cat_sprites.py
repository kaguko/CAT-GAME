"""Generate the 4 × 4 transparent cat sprites used by Turbo Purr Cat.

Run:  py tools/generate_cat_sprites.py
Requires: Pillow
"""
from pathlib import Path
from PIL import Image, ImageDraw

OUT = Path(__file__).parents[1] / "app/src/main/res/drawable-nodpi"
SCALE, OUTPUT = 3, 512
OUTLINE = (62, 42, 30, 255)
EYE = (36, 26, 18, 255)
PALETTES = {
    "orange": ((232, 161, 90, 255), (186, 107, 51, 255), (244, 199, 154, 255), True),
    "black": ((58, 58, 58, 255), (35, 35, 35, 255), (105, 87, 87, 255), False),
    "white": ((242, 239, 230, 255), (218, 215, 205, 255), (250, 200, 200, 255), False),
    "calico": ((217, 179, 140, 255), (110, 75, 55, 255), (244, 199, 154, 255), True),
}
STATES = ("normal", "purring", "deep_purr", "max")


def sc(value):
    return int(round(value * SCALE))


def points(items):
    return [(sc(x), sc(y)) for x, y in items]


def ellipse(draw, box, **kwargs):
    draw.ellipse(tuple(sc(v) for v in box), **kwargs)


def line(draw, items, **kwargs):
    draw.line(points(items), **kwargs)


def arc(draw, box, start, end, **kwargs):
    draw.arc(tuple(sc(v) for v in box), start, end, **kwargs)


def render(skin, state):
    body, stripe, ear, tabby = PALETTES[skin]
    image = Image.new("RGBA", (sc(OUTPUT), sc(OUTPUT)), (0, 0, 0, 0))
    draw = ImageDraw.Draw(image)
    width = sc(5)

    # Ears behind the round face.
    for outer, inner in (
        ([(150, 130), (105, 245), (250, 210)], [(165, 155), (135, 225), (232, 205)]),
        ([(362, 130), (407, 245), (262, 210)], [(347, 155), (377, 225), (280, 205)]),
    ):
        draw.polygon(points(outer), fill=body)
        line(draw, outer + [outer[0]], fill=OUTLINE, width=width, joint="curve")
        draw.polygon(points(inner), fill=ear)

    ellipse(draw, (85, 190, 427, 535), fill=body, outline=OUTLINE, width=width)
    if tabby:
        for start, end, stroke in (
            ((256, 215), (256, 260), 6), ((236, 218), (244, 263), 5),
            ((276, 218), (268, 263), 5), ((216, 230), (228, 270), 4), ((296, 230), (284, 270), 4),
        ):
            line(draw, [start, end], fill=stripe, width=sc(stroke))
        if skin == "calico":
            ellipse(draw, (115, 285, 178, 360), fill=stripe)
            ellipse(draw, (333, 410, 398, 475), fill=(232, 144, 81, 255))

    # Eyes, state dependent.
    for x in (186, 326):
        if state == "normal":
            ellipse(draw, (x - 20, 320, x + 20, 370), fill=EYE)
            ellipse(draw, (x - 8, 331, x + 4, 343), fill=(255, 255, 255, 255))
        elif state == "purring":
            ellipse(draw, (x - 20, 333, x + 20, 370), fill=EYE)
            ellipse(draw, (x - 24, 315, x + 24, 349), fill=body, outline=OUTLINE, width=sc(4))
            ellipse(draw, (x - 6, 348, x + 3, 357), fill=(255, 255, 255, 255))
        else:
            arc(draw, (x - 27, 328 if state == "max" else 333, x + 27, 371), 200, 340, fill=OUTLINE, width=sc(7 if state == "max" else 6))
            if state == "max":
                ellipse(draw, (x - 36, 370, x + 36, 400), fill=(242, 150, 158, 150))

    # Nose, mouth and whiskers.
    draw.polygon(points([(242, 400), (270, 400), (256, 416)]), fill=(226, 124, 124, 255))
    line(draw, [(242, 400), (270, 400), (256, 416), (242, 400)], fill=OUTLINE, width=sc(3), joint="curve")
    if state == "max":
        arc(draw, (195, 410, 317, 475), 20, 160, fill=OUTLINE, width=sc(5))
        ellipse(draw, (238, 443, 274, 469), fill=(234, 123, 139, 255), outline=OUTLINE, width=sc(3))
    else:
        arc(draw, (230, 417, 256, 443), 0, 120, fill=OUTLINE, width=sc(5))
        arc(draw, (256, 417, 282, 443), 60, 180, fill=OUTLINE, width=sc(5))
    whisker = (62, 42, 30, 95 if state in ("deep_purr", "max") else 210)
    for side in (-1, 1):
        for index in range(3):
            y = 400 + (index - 1) * 22
            line(draw, [(256 + side * 150, y), (256 + side * 235, y + (index - 1) * 12)], fill=whisker, width=sc(3))

    return image.resize((OUTPUT, OUTPUT), Image.Resampling.LANCZOS)


def main():
    OUT.mkdir(parents=True, exist_ok=True)
    for skin in PALETTES:
        for state in STATES:
            path = OUT / f"cat_{skin}_{state}.png"
            render(skin, state).save(path)
            print(path.name)


if __name__ == "__main__":
    main()
