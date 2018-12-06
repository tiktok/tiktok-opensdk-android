# encoding: utf-8

# 使用说明：
# python font_swapper.py --view TextView --ignoreBuild True --nameWarning True
# 执行完成后无法处理的日志在font_swapper.log

import fileinput
import os
import re
import logging

import sys
import argparse

text_size = "android:textSize"
include_padding = "android:includeFontPadding"
line_space_extra = "android:lineSpacingExtra"
bold_text = "android:textStyle=\"bold\""

xmlns_android = "http://schemas.android.com/apk/res/android"
xmlns_auto = "xmlns:app=\"http://schemas.android.com/apk/res-auto\""

view_type_dict = {"TextView": "com.bytedance.ies.dmt.ui.widget.DmtTextView",
                  "EditText": "com.bytedance.ies.dmt.ui.widget.DmtEditText",
                  "Button": "com.bytedance.ies.dmt.ui.widget.DmtButton",
                  "RadioButton": "com.bytedance.ies.dmt.ui.widget.DmtRadioButton"}


def get_layout_files(path, ignore_build):
    res = []
    list_dirs = os.walk(path)
    for root, dirs, files in list_dirs:
        for f in files:
            file = os.path.join(root, f)
            if re.match(".*/build/.*", file) and ignore_build == "True":
                continue
            if re.match(".*layout/.*\.xml$", file) is not None:
                res.append(file)
    return res

def hasString(file, pattern):
    fp = open(file, "r")
    if fp.read().find(pattern) != -1:
        return True
    return False

def getSizeStyle(size):
    if size == 9 or size == 10 or size == 11:
        return "style=\"@style/font_size_20\""
    elif 11 < size < 15:
        return "style=\"@style/font_size_" + str(size * 2) + "\""
    elif size == 15 or size == 16:
        return "style=\"@style/font_size_30\""
    elif size == 17 or size == 18:
        return "style=\"@style/font_size_34\""
    elif size == 19 or size == 20 or size == 21:
        return "style=\"@style/font_size_40\""
    elif 28 <= size <= 35:
        return "style=\"@style/font_size_72\""
    else:
        return None


def process(file, view_from, view_to, name_waring):
    print("processing:" + file)
    # 是否有必要引入xmlns:app
    insert_xmlns_auto = False

    # 进入TextView代码块标志
    enter_flag = 0
    fileinput.close()
    file_input_str = fileinput.input(file, inplace=1)
    line_number = 0
    for line in file_input_str:
        line_number = line_number + 1
        if line.__contains__("<" + view_from):
            enter_flag = 1
            line = line.replace(view_from, view_to)
        elif line.__contains__(text_size) and enter_flag == 1:
            size_list = re.findall(r"\d+\.?\d*", line)
            if size_list.__len__() == 1:
                size = int(float(size_list[0]))
                size_style = getSizeStyle(size)
                if size_style is not None:
                    line = line.replace(line.strip(' \t\n\r/>'), getSizeStyle(size))
                else:
                    logging.error("VALID SIZE: " + str(size) + file + ": %d" % line_number)
        elif line.__contains__(bold_text) and enter_flag == 1:
            print line,
            line = line.replace(line.strip(' \t\n\r/>'), "app:fontType=\"bold\"")
            insert_xmlns_auto = True
        elif line.strip(' \t\n\r').startswith("style") and enter_flag == 1:
            # 如果遇到类似这种类型的style="@style/text_15_s1"
            lint_str = line.strip(' \t\n\r/>')
            if re.match(r'style=\"@style/text_\d+_s\d*\"', lint_str) is not None:
                style_list = lint_str.split("_")
                color = style_list[-1]
                line = line.replace(line.strip(' \t\n\r/>'), "android:textColor=\"@color/" + color)
                size = style_list[-2]
                size_style = getSizeStyle(int(size))
                if size_style is not None:
                    line += line.replace(line.strip(' \t\n\r/>'), size_style)
                else:
                    logging.error("VALID STYLE_SIZE: " + file + ": %d" % line_number)
            else:
                logging.error("VALID STYLE: " + file + ": %d" % line_number)

        elif line.__contains__(include_padding) and enter_flag == 1:
            continue
        elif line.__contains__(line_space_extra) and enter_flag == 1:
            continue
        elif line.strip(' \t\n\r').endswith("/>"):
            enter_flag = 0
        elif line.strip(' \t\n\r').startswith("<") and line.strip(' \t\n\r').endswith(view_from) \
                and not line.strip(' \t\n\r').endswith("Dmt" + view_from ) and name_waring == "True":
            logging.warning("SHOULD_MANUAL_PROCESS: " + file + ": %d" % line_number)
            logging.warning("name : "+line)
        print line,

    # 如果是需要添加是否有必要引入xmlns:app
    insert_xmlns_auto = insert_xmlns_auto and not hasString(file, xmlns_auto)
    fileinput.close()
    file_input_str = fileinput.input(file, inplace=1)
    for line in file_input_str:
        if line.__contains__(xmlns_android) and insert_xmlns_auto:
            line += "    " + xmlns_auto + "\n"
        print line,



if __name__ == '__main__':
    logging.basicConfig(level=logging.DEBUG,
                        format='%(asctime)s %(filename)s[line:%(lineno)d] %(levelname)s %(message)s',
                        datefmt='%a, %d %b %Y %H:%M:%S',
                        filename='font_swap.log',
                        filemode='w')

    parser = argparse.ArgumentParser()
    parser.add_argument('--view', type=str, default='TextView')
    parser.add_argument('--ignoreBuild', type=str, default='False')
    parser.add_argument('--nameWarning', type=str, default='True')
    args = parser.parse_args()
    print args.view
    print args.ignoreBuild
    print args.nameWarning

    layout_files = get_layout_files(os.getcwd(), args.ignoreBuild)

    if layout_files is None or len(layout_files) == 0:
        print("没有找到布局文件")
        os._exit(0)

    view_change_to = view_type_dict[args.view]
    for file in layout_files:
        process(file, args.view, view_change_to, args.nameWarning)

    print("ok，大功告成！！")
