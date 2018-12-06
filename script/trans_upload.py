# 在工程根目录下，执行命令：python trans_upload.py

import os

CROWDING_KEY = 'c48012205e14c47968ea24ea977ba042'
PROJECT_NAME = 'musically-tiktok-zh-en'
CMD = "curl -F \"files[%s]=@%s\" https://api.crowdin.com/api/project/%s/update-file?key=%s"


def get_online_file_path(file_name):
    return "Android/%s" % file_name


def update_file(file_name, local_file_path):
    os.system(CMD % (get_online_file_path(file_name), local_file_path, PROJECT_NAME, CROWDING_KEY))


if __name__ == '__main__':
    update_file("strings.xml", "app/src/main/res/values/strings.xml")
    update_file("tiktok-strings.xml", "app/src/tiktok/res/values/tiktok-strings.xml")
    update_file("mus-strings.xml", "app/src/musically/res/values/mus-strings.xml")
