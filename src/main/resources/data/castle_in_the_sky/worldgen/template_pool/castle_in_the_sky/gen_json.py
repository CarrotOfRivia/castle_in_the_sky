import json
from copy import deepcopy
with open("laputa000.json") as f:
    src = json.load(f)

element = src["elements"][0]
src["elements"] = []

for x in [0, 1, 2]:
    for z in [0, 1, 2]:
        for y in [0, 1, 2]:
            this_element = deepcopy(element)
            this_element["element"]["location"] = f"castle_in_the_sky:laputa{x}{y}{z}"
            src["elements"].append(this_element)

with open("laputa000_.json", "w") as f_out:
    json.dump(src, f_out, indent=2)
