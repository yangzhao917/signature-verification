#!/bin/bash
###
### 用于分发文件到目标服务器
###
### Usage:
###   ./distribute_script.sh
### 根据需要修改配置文件信息
###

# 分发的文件列表
file_list=("signature.sh")

# 定义远程服务器信息
remote_file_dir="/home"     # 远程的文件的存放位置（绝对路径）
remote_username="root"      # 远程主机用户名
remote_userpass="Asdf3.14"  # 远程主机密码


# 分发的目标机器列表
target_machine_list=(
    "172.25.233.11"
)

# 检查expect是否安装
isInstalledExpect() {
    if ! command -v expect > /dev/null 2>&1; then
        # 根据不同的Linux发行版执行不同的安装命令
        if [ -x "$(command -v apt-get)" ]; then
            apt-get update && sudo apt-get install -y expect
        elif [ -x "$(command -v yum)" ]; then
            yum install -y expect
        fi
        echo "expect已安装完成。"
    fi
}

# 拷贝文件到远程服务器
copyFileToRemote() {
    file=$1
    ip=$2
    expect > /dev/null <<EOF
            # 发送文件到远程服务器
            spawn scp $file $remote_username@$ip:$remote_file_dir
            expect {
                "yes/no" { send "yes\n";exp_continue }
                "*password*" { send "$remote_userpass\n" }
            }
            expect eof # 这行必须有，不然文件发送不过去
EOF
}

main() {
    beginTime=`date +%s`
    isInstalledExpect
    # 获取文件列表
    for file in "${file_list[@]}"; do {
        # 多线程分发到远程主机
        for ip in "${target_machine_list[@]}"; do {
            $(ping -W10 -c 1 $ip > /dev/null 2>&1)
            # $?：判断上一条命令的执行状态，如果为0表示成功，非0表示失败
            if [ $? -eq 0 ]; then
                copyFileToRemote $file $ip
            else
                # IP不通
                echo "$ip 不通"
            fi
        } &
        done
    } done
    wait

    endTime=`date +%s`
    echo "总共耗时:" $(($endTime-$beginTime)) "秒"
}

main