from glob import glob
for f_src in glob("blue_*.json"):
    src = open(f_src).read()
    for color in ["red", "yellow"]:
        f_out = open(f_src.replace("blue", color), "w")
        f_out.write(src.replace("blue", color))
