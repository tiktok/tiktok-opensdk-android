# 穿山甲打包测试工具

1.将pangolin.conf拷贝到外部目录（防止git check覆盖）

2.修改pangolin.conf文件中的custom_app_git_tag # custom app git tag
custom_app_git_tag = new_develop_test_2

3.修改pangolin_test_build.sh路径

4.工程根目录下执行  ./script/sh_dev/pangolin/pangolin_test_build.sh

其他
构建过程会git checkout 到该Tag

