"""
author：谭乐华<tanlehua@bytedance.com>
在工程根目录下，执行命令：
python3 string_xml_merger.py tiktok
或者
python3 string_xml_merger.py musically
执行完后，在工程目录下会生成一个strings.xml文件，这个文件就是ResourceMerge后的strings.xml
"""

import xml.etree.ElementTree as ET
import os
import sys
from shutil import copyfile


def alterGradle():
    # backup
    file_path = 'app/build.gradle'
    parent_dir = os.path.abspath(os.path.join(file_path, os.path.pardir))
    copyfile(file_path, os.path.join(parent_dir, 'build.gradle.bak'))

    alterContent = ("\ntasks.whenTaskAdded { task ->\n"
                    "if (task.name.matches('merge\\\w*Resources')) {\n"
                    "task.doLast {\n"
                    "throw new GradleException('End up with mergeResources')\n"
                    "}\n"
                    "}\n"
                    "}\n")
    f = open(file_path, 'a')
    f.write(alterContent)
    f.close()


def resetGradle():
    file_path = 'app/build.gradle'
    parent_dir = os.path.abspath(os.path.join(file_path, os.path.pardir))
    bak = os.path.join(parent_dir, 'build.gradle.bak')
    if os.path.exists(bak):
        copyfile(bak, file_path)
        os.remove(bak)


if __name__ == "__main__":
    flavor = sys.argv[1]
    alterGradle()
    cmd = './gradlew clean assemble%sRelease' % flavor
    os.system(cmd)
    resetGradle()
    xmlPath = 'app/build/intermediates/res/merged/%s/release/values/values.xml' % flavor
    if os.path.exists(xmlPath):
        assert xmlPath + 'not exist'
    tree = ET.parse(xmlPath)
    root = tree.getroot()
    children = root.getchildren()
    for child in children:
        if child.tag != 'string' and child.tag != 'string-array':
            root.remove(child)
            continue
        strName = child.attrib['name']
        if strName.startswith('common_google_play') or strName.startswith(
                'com_facebook') or strName.startswith(
            'com_kakao') or strName.startswith('com_taobao') or strName.startswith(
            'com_accountkit'):
            root.remove(child)
            continue
        if 'translatable' in child.attrib:
            root.remove(child)

    outputFile = 'strings.xml'
    tree.write(outputFile, 'utf-8')

    stringsFile = open(outputFile, 'r')
    allLines = stringsFile.readlines()
    stringsFile.close()

    stringsFile = open(outputFile, 'w')
    stringsFile.writelines('<?xml version="1.0" encoding="utf-8"?>')
    for line in allLines:
        if line.find(
                '<resources xmlns:ns0="http://schemas.android.com/tools" xmlns:ns1="urn:oasis:names:tc:xliff:document:1.2">') != -1:
            stringsFile.writelines(line.replace(
                '<resources xmlns:ns0="http://schemas.android.com/tools" xmlns:ns1="urn:oasis:names:tc:xliff:document:1.2">',
                '<resources>'))
        elif line.find('ns0') != -1 or line.find('ns1') != -1:
            continue
        else:
            stringsFile.write(line)
    stringsFile.close()
