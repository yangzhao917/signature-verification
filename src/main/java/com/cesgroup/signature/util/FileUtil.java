package com.cesgroup.signature.util;

import cn.hutool.core.net.NetUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：Yangzhao
 * @description：文件操作工具类
 * @date ：2023/8/30 2:57 上午
 * @version: 1.0
 */
@Slf4j
public class FileUtil {

    /**
     * 计算文件的SHA256哈希值
     * @param filePath 文件路径
     * @return 字节数组
     * @throws IOException IO异常
     * @throws NoSuchAlgorithmException NoSearch算法异常
     */
    public static byte[] calculateSHA256(String filePath) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (
            FileInputStream fis = new FileInputStream(filePath);
            FileChannel channel = fis.getChannel();
            DigestInputStream dis = new DigestInputStream(fis, digest)) {
            ByteBuffer buffer = ByteBuffer.allocate(8192); // 8 KB buffer
            while (channel.read(buffer) != -1) {
                buffer.flip();
                digest.update(buffer);
                buffer.clear();
            }
            return digest.digest();
        }
    }

    /**
     * 将字节数组转换为String类型哈希值
     * @param bytes 字节数组
     * @return 哈希值
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
//        /** 测试文件SHA256加密 */
//        long startTime = System.currentTimeMillis();
//        String sha256 = bytesToHex(calculateSHA256("/Users/yangzhao/Desktop/rsyslog-bak/172.25.16.12/172.25.16.12_2023-08-25.log"));
//        long endTime = System.currentTimeMillis();
//
//        System.out.println("SHA-256：" + sha256);
//        System.out.println("耗时：" + (endTime - startTime) + "ms");

//        /** 测试获取指定目录下的所有文件,过滤隐藏文件 */
//        String path = "/Users/yangzhao/Desktop/rsyslog-bak";
//        List<File> files = getAllFiles(path);
//        for (File file : files) {
//            System.err.println(file.getPath());
//        }
//        System.out.println("文件数量：" + files.size());

        String localhost = NetUtil.getLocalhostStr();
        System.out.println(localhost);
    }

    /**
     * @author: yangzhao
     * @description: 递归获取指定目录下的所有文件，不包含隐藏文件
     * @date: 2023/8/30 2:08 下午
     * @param path 目录绝对路径
     * @return 目录下的文件列表
     */
    public static List<File> getAllFiles(String path) {
        List<File> fileList = new ArrayList<>();
        // 检查指定路径是否为目录
        File directory = new File(path);
        if (!directory.isDirectory()) {
            return fileList;
        }
        // 获取目录下的所有文件和子目录
        File[] files = directory.listFiles();
        // 遍历所有文件和子目录
        for (File file : files) {
            // 如果是文件则添加到文件列表中，过滤隐藏文件
            if (file.isFile() && !file.getName().startsWith(".")) {
                fileList.add(file);
            }
            // 如果是目录则递归调用该方法
            if (file.isDirectory()) {
                fileList.addAll(getAllFiles(file.getAbsolutePath()));
            }
        }
        return fileList;
    }

    /**
     * @author: yangzhao
     * @description: 递归获取指定目录下的所有文件，不包含隐藏文件
     * @date: 2023/8/30 2:08 下午
     * @param pathList 目录绝对路径
     * @return 目录下的文件列表
     */
    public static List<File> getAllFiles(List<String> pathList) {
        List<File> fileList = new ArrayList<>();
        for (String path : pathList) {
            // 检查指定路径是否为目录
            File directory = new File(path);
            if (!directory.isDirectory()) {
                return fileList;
            }
            // 获取目录下的所有文件和子目录
            File[] files = directory.listFiles();
            // 遍历所有文件和子目录
            for (File file : files) {
                // 如果是文件则添加到文件列表中，过滤隐藏文件
                if (file.isFile() && !file.getName().startsWith(".")) {
                    fileList.add(file);
                }
                // 如果是目录则递归调用该方法
                if (file.isDirectory()) {
                    fileList.addAll(getAllFiles(file.getAbsolutePath()));
                }
            }
        }

        return fileList;
    }

    /**
     * 计算文件的SHA256哈希值并获取签名后的值
     * @author: yangzhao
     * @date: 2023/8/30 2:53 下午
     * @param fileList 计算的文件对象
     * @return key为文件名，value为文件哈希值
     */
    public static List<Map<String, String>> calculateAllFileHash(List<File> fileList) throws IOException, NoSuchAlgorithmException {
        List<Map<String, String>> fileHashList = new ArrayList<>();
        for (File file : fileList) {
            Map<String, String> map = new HashMap<>();
            byte[] bytes = FileUtil.calculateSHA256(file.getPath());
            String fileHashVal = FileUtil.bytesToHex(bytes);
            map.put(file.getPath(), fileHashVal);
            fileHashList.add(map);
        }
        return fileHashList;
    }


    /**
     * 计算文件的SHA256哈希值
     * @author: yangzhao
     * @date: 2023/8/30 2:53 下午
     * @param fileList 计算的文件对象
     * @return key为文件名，value为文件哈希值
     */
    public static Map<String, String> getFileHash(List<File> fileList) throws IOException, NoSuchAlgorithmException {
        Map<String, String> map = new HashMap<>();
        for (File file : fileList) {
            byte[] bytes = FileUtil.calculateSHA256(file.getPath());
            String fileHashVal = FileUtil.bytesToHex(bytes);
            map.put(file.getPath(), fileHashVal);
        }
        return map;
    }

    /**
     * 检查文件是否存在
     * @author: yangzhao
     * @date: 2023/9/1 1:38 上午
     * @param filePath 文件绝对路径
     * @return 存在返回true，否则返回false
     */
    public static boolean isFileExists(String filePath){
        File file = new File(filePath);
        if (!file.exists()){
            return false;
        }
        return true;
    }

}
