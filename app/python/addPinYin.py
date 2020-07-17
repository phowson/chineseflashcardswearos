from xpinyin import Pinyin
import sys
p = Pinyin()


out = []

with open(sys.argv[1], 'r', encoding='utf-8') as f:
    lines = f.readlines()
    for l in lines:
        l = l.strip()
        if len(l)>0:
            cols = l.split(",")
            out.append(cols[0] +","+ p.get_pinyin(cols[0], '', tone_marks='marks') +"," + cols[1] +"\n")
         

with open(sys.argv[1], 'w', encoding='utf-8') as f:
    for l in out:
        f.write(l)
