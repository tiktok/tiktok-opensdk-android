#!/usr/bin/env python3
# coding=utf-8
# 国际版替换string自动化脚本

import os
import shutil
import threading

EN_CROWDING_KEY = '9fd662cf1095252bea9886af1a73e7c0'
EN_PROJECT_NAME = 'musically-tiktok-en'

ZH_EN_CROWDING_KEY = "c48012205e14c47968ea24ea977ba042"
ZH_EN_PROJECT_NAME = "musically-tiktok-zh-en"

TRANSLATION_TMP = 'translation_tmp'


def clear_cache():
    print('clear last cache ...')
    if os.path.isdir('./' + TRANSLATION_TMP):
        os.system("rm -rf " + TRANSLATION_TMP)
    os.system("mkdir " + TRANSLATION_TMP)
    os.system("mkdir " + TRANSLATION_TMP + "/" + EN_PROJECT_NAME)
    os.system("mkdir " + TRANSLATION_TMP + "/" + ZH_EN_PROJECT_NAME)


def build_current_version(project_name, key):
    print('building...')
    os.system(
        "curl -s https://api.crowdin.com/api/project/" + project_name + "/export?key=" + key)


def download_translation(project_name, key):
    print('downloading...')
    os.system(
        "curl -s -o ./" + TRANSLATION_TMP + "/" + project_name + "/all.zip https://api.crowdin.com/api/project/"
        + project_name + "/download/all.zip?key=" + key)


def deal_download_file(project_name):
    os.system("unzip ./" + TRANSLATION_TMP + "/" + project_name + "/all.zip" + " -d "
              + "./" + TRANSLATION_TMP + "/" + project_name)
    update_string_files(project_name, "../app/src/i18n/res", "strings.xml")
    update_string_files(project_name, "../app/src/tiktok/res", "tiktok-strings.xml")
    update_string_files(project_name, "../app/src/musically/res", "mus-strings.xml")


def update_string_files(project_name, values_dirs, file_name):
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
        update_file = "./%s/%s/%s/Android/%s" % (TRANSLATION_TMP, project_name, lang, file_name)
        if os.path.exists(update_file):
            shutil.copy(update_file, strings_file)
        else:
            print('%s language update file not found.' % update_file)


def update_en_translate_file():
    build_current_version(ZH_EN_PROJECT_NAME, ZH_EN_CROWDING_KEY)
    download_translation(ZH_EN_PROJECT_NAME, ZH_EN_CROWDING_KEY)
    deal_download_file(ZH_EN_PROJECT_NAME)


def update_others_translate_file():
    build_current_version(EN_PROJECT_NAME, EN_CROWDING_KEY)
    download_translation(EN_PROJECT_NAME, EN_CROWDING_KEY)
    deal_download_file(EN_PROJECT_NAME)


if __name__ == '__main__':
    clear_cache()
    # 默认语言全部是英文，后续不再单独合入英文小语种
    # t1 = threading.Thread(target=update_en_translate_file())
    t2 = threading.Thread(target=update_others_translate_file())
    # t1.start()
    t2.start()
    # t1.join()
    t2.join()
    clear_cache()
