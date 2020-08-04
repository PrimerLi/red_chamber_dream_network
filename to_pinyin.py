# -*- coding: utf-8 -*-
import os
from pypinyin import pinyin, lazy_pinyin, Style

def to_pinyin(vertex_file_name, output_file_name):
    assert(os.path.exists(vertex_file_name))
    reader = open(vertex_file_name, "r")
    lines = reader.readlines()
    reader.close()
    vertices = map(lambda line: line.strip("\n").decode("utf-8"), lines)
    def name_to_pinyin(name):
        a = lazy_pinyin(name)
        surname = a[0][0].upper() + a[0][1:]
        first_name = "".join(a[1:])
        first_name = first_name[0].upper() + first_name[1:]
        return surname + " " + first_name
    
    writer = open(output_file_name, "w")
    D = dict()
    for name in vertices:
        pinyin = name_to_pinyin(name)
        D[name] = pinyin
        writer.write(name.encode("utf-8") + " -> " + pinyin.encode("utf-8") + "\n")
    writer.close()
    return D

def read_name_pinyin(name_pinyin_file_name):
    assert(os.path.exists(name_pinyin_file_name))
    D = dict()
    reader = open(name_pinyin_file_name, "r")
    for (index, string) in enumerate(reader):
        a = string.strip("\n").split(" -> ")
        name = a[0].decode("utf-8")
        pinyin = a[1]
        D[name] = pinyin
    reader.close()
    return D

def translate_edges(edge_file_name, name_pinyin_D, output_file_name):
    assert(os.path.exists(edge_file_name))
    reader = open(edge_file_name, "r")
    writer = open(output_file_name, "w")
    for (index, string) in enumerate(reader):
        a = string.strip("\n").decode("utf-8").split(";")
        left = a[0]
        right = a[1]
        writer.write(name_pinyin_D[left] + ";" + name_pinyin_D[right] + ";" + a[2] + "\n")
    reader.close()
    writer.close()

def main():
    import sys
    D = read_name_pinyin("name_pinyin.txt")
    translate_edges("edges.csv", D, "edges_pinyin.csv")
    return 0

if __name__ == "__main__":
    import sys
    sys.exit(main())
