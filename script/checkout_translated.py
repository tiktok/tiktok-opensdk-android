#!/usr/bin/env python3
# coding=utf-8
# 国际版替换string自动化脚本

import os
import threading
import xml.etree.ElementTree as ET
import sys

def checkout_all_files():
    # os.system("unzip ./" + TRANSLATION_TMP + "/" + project_name + "/all.zip" + " -d "
    #           + "./" + TRANSLATION_TMP + "/" + project_name)
    update_string_files("./app/src/i18n/res", "strings.xml")
    update_string_files("./app/src/tiktok/res", "tiktok-strings.xml")
    update_string_files("./app/src/musically/res", "mus-strings.xml")


def fill_value_by_key(input_file, output_file, key_file):
    input_file_path = os.path.abspath(input_file)
    output_file_path = os.path.abspath(output_file)
    key_file_path = os.path.abspath(key_file)

    print("+++++++++++++++read++++++++++++++++")

    print("read " + key_file_path)
    try:
        key_tree = ET.parse(key_file_path)
        print ("tree type:", type(key_tree))

        # 获得根节点
        key_root = key_tree.getroot()
    except Exception as e:  #捕获除与程序退出sys.exit()相关之外的所有异常
        print ("parse " + key_file_path + "fail!")
        sys.exit()


    print("read " + input_file_path)
    try:
        input_tree = ET.parse(input_file_path)
        print ("tree type:", type(input_tree))

        # 获得根节点
        input_root = input_tree.getroot()
    except Exception as e:  #捕获除与程序退出sys.exit()相关之外的所有异常
        print ("parse " + input_file_path + "fail!")
        sys.exit()

    print("read " + output_file_path)
    try:
        output_tree = ET.parse(output_file_path)
        print ("tree type:", type(output_tree))

        # 获得根节点
        output_root = output_tree.getroot()
    except Exception as e:  #捕获除与程序退出sys.exit()相关之外的所有异常
        print ("parse " + output_file_path + "fail!")
        sys.exit()

    print("+++++++++++++++deal++++++++++++++++")

    dictionary_input = {}
    for input in input_root:
        if (input.tag != "string") or (input.tag != "string-array"):
            continue
        translatable = input.get("translatable")
        if translatable == "false":
            continue
        if input.tag == "string":
            key = input.get("name")
            dictionary_input[key] = input
        if input.tag == "string-array":
            key = input.get("name")
            dictionary_input[key] = input

    dictionary_output = {}
    for output in output_root:
        if (output.tag != "string") or (output.tag != "string-array"):
            continue
        translatable = output.get("translatable")
        if translatable == "false":
            continue
        if output.tag == "string":
            key = output.get("name")
            dictionary_output[key] = output
        if output.tag == "string-array":
            key = output.get("name")
            dictionary_output[key] = output

    for child in key_root:
        if (child.tag != "string") or (child.tag != "string-array"):
            continue
        translatable = child.get("translatable")
        if translatable == "false":
            continue
        if child.tag == "string":
            key = child.get("name")
            if key in dictionary_input.keys():
                item = dictionary_input[key]
                value = item.text
                if key in dictionary_output.keys():
                    item_output = dictionary_output[key]
                    item_output.text = value
                else:
                    node = ET.Element("string")
                    node.set("name", key)
                    node.text = value
                    output_root.append(node)
            else:
                print ("there is a un-translated key :" + key + " ---" + input_file)
        else:
            key = child.get("name")
            if key in dictionary_input.keys():
                item = dictionary_input[key]
                if key in dictionary_output.keys():
                    item_output = dictionary_output[key]
                    array_itmes = item_output.getchildren()
                    for array_item in array_itmes:
                        item_output.remove(array_item)
                    input_array_items = item.getchildren()
                    for input_array_item in input_array_items:
                        node = ET.Element("item")
                        node.text = input_array_item.text
                        item_output.append(node)

                else:
                    array_node = ET.Element("string-array")
                    array_node.set("name", key)
                    input_array_items = item.getchildren()
                    for input_array_item in input_array_items:
                        node = ET.Element("item")
                        node.text = input_array_item.text
                        array_node.append(node)
                    output_root.append(array_node)
            else:
                print ("there is a un-translated key :" + key + " ---" + input_file)

    output_tree.write(output_file, encoding="utf-8", xml_declaration=True)

def check_value_by_key(output_file, key_file):
    output_file_path = os.path.abspath(output_file)
    key_file_path = os.path.abspath(key_file)

    # print("+++++++++++++++read++++++++++++++++")

    # print("read " + key_file_path)
    try:
        key_tree = ET.parse(key_file_path)
        # print ("tree type:", type(key_tree))

        # 获得根节点
        key_root = key_tree.getroot()
    except Exception as e:  #捕获除与程序退出sys.exit()相关之外的所有异常
        print ("parse " + key_file_path + "fail!")
        sys.exit()

    # print("read " + output_file_path)
    try:
        output_tree = ET.parse(output_file_path)
        # print ("tree type:", type(output_tree))

        # 获得根节点
        output_root = output_tree.getroot()
    except Exception as e:  #捕获除与程序退出sys.exit()相关之外的所有异常
        print ("parse " + output_file_path + "fail!")
        sys.exit()

    # print("+++++++++++++++deal++++++++++++++++")

    dictionary_output = {}
    for output in output_root:
        if (output.tag != "string") and (output.tag != "string-array"):
            continue
        translatable = output.get("translatable")
        if translatable == "false":
            continue
        if output.tag == "string":
            key = output.get("name")
            dictionary_output[key] = output
        if output.tag == "string-array":
            key = output.get("name")
            dictionary_output[key] = output

    result = []
    for child in key_root:
        if (child.tag != "string") and (output.tag != "string-array"):
            # print ("not string :" + child.tag)
            continue
        translatable = child.get("translatable")
        if translatable == "false":
            # print ("translatable = false")
            continue

        key = child.get("name")

        if key not in dictionary_output.keys():
            # print ("there is a un-translated key :" + key)
            result.append(key)
            # sys.exit()
    return result


def update_string_files(values_dirs, file_name):
    tatolkeys = []
    print (file_name + ":")
    for x in os.listdir(values_dirs):
        if not x.startswith("values-"):
            continue
        lang = x.split("-")[1]
        if lang == 'v21':
            continue
        # 适配语言码
        if lang == "in":
            lang = "id"
        elif lang == 'sv':
            lang = 'sv-SE'
        elif lang == 'bn':
            lang = 'bn-IN'
        elif lang == 'es':
            lang = 'es-ES'
        elif lang == 'gu':
            lang = 'gu-IN'
        elif lang == 'ml':
            lang = 'ml-IN'
        elif lang == 'pa':
            lang = 'pa-IN'
        elif lang == 'pt':
            lang = 'pt-BR'
        elif lang == 'zh':
            lang = 'zh-TW'
        elif lang == 'ns':
            lang = 'nso'
        strings_file = values_dirs + "/" + x

        if os.path.exists(strings_file):
            # shutil.copy(update_file, strings_file)
            # fill_value_by_key(update_file, strings_file + "/" + file_name, values_dirs + "/values/" + file_name)
            keys = check_value_by_key(strings_file + "/" + file_name, values_dirs + "/values/" + file_name)

            for key in keys:
                if key not in tatolkeys:
                    tatolkeys.append(key)
        else:
            print('%s language update file not found.' % strings_file)

    for key in tatolkeys:
        print ("     untranslated: " + key)




if __name__ == '__main__':
    t2 = threading.Thread(target=checkout_all_files())
    t2.start()
    t2.join()
