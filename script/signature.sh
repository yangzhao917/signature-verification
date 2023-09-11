#!/bin/bash
###
### 用于签名验签定时自动化的实现
###
### Usage:
###   ./signature Options
###
### Options:
###   --sign     签名
###   --verify   验签
###   -h         显示脚本帮助信息

# 签名、验签接口（正式）
# SIGN_API_URL="http://172.25.16.29:48080/sign-verify/signature/saveOrUpdate"
# VERIFY_API_URL="http://172.25.16.29:48080/sign-verify/verify/server"

# 签名、验签接口（开发）
SIGN_API_URL="http://localhost:48080/sign-verify/signature/saveOrUpdate"
VERIFY_API_URL="http://localhost:48080/sign-verify/verify/server"

# 获取客户端IP
CLIENT_IP="$(hostname -I | awk '{print $1}')"  # linux地址的获取
# CLIENT_IP="$(ifconfig en0 | grep inet | grep -v inet6 | awk '{print $2}')" # mac地址的获取

# 定义接口日志存放目录
api_log_dir="$(cd $(dirname $0); pwd)/log"

# 互联网机器文件存放目录
INTERNAT_HOST_DIR="/data/internet-host-log"

# 定义待签的文件、目录列表
file_list=(
    "/data/rsyslog-bak"
    "/etc/pam.d/su"
    "/etc/sudoers"
    "/etc/passwd"
    "/etc/passwd-"
    "/etc/shadow"
    "/etc/shadow-"
    "/etc/gshadow"
    "/etc/gshadow-"
    "/etc/group"
    "/etc/group-"
)

help() {
    awk -F'### ' '/^###/ { print $2 }' "$0"
}

# 获取文件的sha256
get_file_sha256() {
    # 获取方法传递的参数 get_file_sha256 "$log_file" "$options"
    # $1：表示获取get_file_sha256函数传递的第一位参数，$2表示第二位参数
    file=$1
    options=$2

    echo "====> 文件处理中：$file"
    # 获取文件的SHA256签名
    signature_result=$(sha256sum "$file")
    # 获取当前时间
    timestamp=$(date +"%Y-%m-%d %H:%M:%S")
    # 提取文件的SHA256签名（没有前缀和空格）
    sign_value=$(echo "${signature_result}" | awk '{print $1}')
    # 提取文件路径（没有前缀和空格）
    file_path=$(echo "${signature_result}" | awk '{print $2}')
    # 发送POST请求
    param_data="filePath=$file_path&fromIp=$CLIENT_IP&signValue=$sign_value"
    send_requset "$param_data" "$options"
    echo -e "<==== 文件处理完成 \n"
}


send_requset() {
    param_data=$1
    options=$2

    if [[ $options == "signature" ]]; then
        response_body=$(curl -s -X POST -d "$param_data" "$SIGN_API_URL")
        echo -e "发送请求到签名接口：$SIGN_API_URL \n请求参数：$param_data \n响应内容：$response_body"
    elif [[ $options == "verification" ]]; then
        response_body=$(curl -s -X POST -d "$param_data" "$VERIFY_API_URL")
        echo -e "发送请求到验签接口：$VERIFY_API_URL \n请求参数：$param_data \n响应内容：$response_body"
    else
        echo "参数错误..."
        exit 1
    fi   
}

install_sha256() {
    # 检查是否安装了sha256sum，如果没有安装则进行安装
    # 安装时需要检查是否配置apt源
    # 政务外网：deb [by-hash=force] http://172.23.1.32/enterprise-packages.chinauos.com/server-enterprise/  fou/sp3  main contrib non-free
    # 互联网：deb [trusted=yes] http://172.22.1.32/enterprise-packages.chinauos.com/server-enterprise/  fou/sp3  main contrib non-free
    if ! command -v sha256sum >/dev/null 2>&1; then
        echo "sha256sum未安装，正在进行安装..."
        # 根据不同的Linux发行版执行不同的安装命令
        if [ -x "$(command -v apt-get)" ]; then
            sudo apt-get update && sudo apt-get install -y sha256sum
        elif [ -x "$(command -v yum)" ]; then
            sudo yum install -y sha256sum
        elif [ -x "$(command -v dnf)" ]; then
            sudo dnf install -y sha256sum
        else
            echo "无法确定Linux发行版，无法安装sha256sum。"
            exit 1
        fi
        echo "sha256sum已安装完成。"
    fi
}

main(){

    beginTime=`date +%s`
    options=$1

    # 获取所有文件和目录，并对文件进行SHA256签名
    for file in "${file_list[@]}"; do 
    {
        # 文件的处理
        if [ -f "$file" ]; then
            # 对每个文件进行SHA256签名
            get_file_sha256 "$file" "$1"
        else
            # 如果是rsyslog日志目录
            if [[ $file =~ "rsyslog-bak" ]] ; then
                param=""
                if [[ "$options" == "signature" ]]; then
                    # 签名操作：查询前一天
                    param="-mtime 1" 
                fi
                log_files=$(find "$file" ${param} -type f -not -path '*/\.*')
                # 查询该目录前一天生成的文件,排除隐藏文件
                for log_file in $log_files; do {
                    get_file_sha256 "$log_file" "$options"
                }
                done
            else
                log_files=$(find "$file" -type f -not -path '*/\.*')
                # 查询该目录前一天生成的文件,排除隐藏文件
                for log_file in $log_files; do {
                    get_file_sha256 "$log_file" "$options"
                }
            fi
        fi
    } &
    done
    wait

    endTime=`date +%s`
    echo "总共耗时:" $(($endTime-$beginTime)) "秒"
}

# 生成日志目录
generate_log_dir() {
    if [ ! -d "${api_log_dir}" ]; then
        mkdir -p "${api_log_dir}"
    fi
}

if [[ $# == 0 || "$1" == "-h" ]]; then
    help
elif [[ $# == 0 || "$1" == "--sign" ]]; then
    generate_log_dir
    # 签名操作，并将生成输出到文件
    main "signature" > "${api_log_dir}/signature_$(date +"%Y%m%d").log"
elif [[ $# == 0 || "$1" == "--verify" ]]; then
    generate_log_dir
    # 验签操作，并将生成输出到文件
    main "verification" > "${api_log_dir}/signature_$(date +"%Y%m%d").log"
else
    echo -e "参数错误... \n参考:./signature -h"
    exit 1
fi