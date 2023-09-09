package com.cesgroup.signature.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cesgroup.signature.entity.SignRecord;
import com.cesgroup.signature.enums.SignTypeEnum;

import java.util.List;
import java.util.Map;

/**
 * @author: yangzhao
 * @description: 签名业务接口，用于处理签名的业务逻辑
 * @date: 2023/8/29 9:00 下午
 */
public interface ISignReocrdService extends IService<SignRecord> {

    /**
     * 本地文件签名
     * @author: yangzhao
     * @date: 2023/8/29 9:02 下午
     * @param
     * @return void
     */
    void localSignature() throws Exception;

    /**
     * @author: yangzhao
     * @description: 服务端签名
     * @date: 2023/8/29 9:02 下午
     * @param
     * @return void
     */
    void serverSignature(SignRecord sign);

    /**
     * @author: yangzhao
     * @description: 批量保存签名到数据库
     * @date: 2023/8/30 3:13 下午
     * @param mapList
     * @return java.lang.Boolean
     */
    void batchSave(List<Map<String, String>> mapList);

    /**
     * 根据文件路径获取签名记录
     * @param path 文件路径
     * @param signType 签名类型
     * @return
     */
    SignRecord findByFilePathAndSignType(String path, SignTypeEnum signType);

    /**
     * 根据文件路径获取签名记录
     * @param path 文件路径
     * @param clientIp 客户端IP地址
     * @return
     */
    SignRecord findByFilePathAndClientIp(String path, String clientIp);

    /**
     * 根据签名记录ID删除签名记录
     */
    void deleteById(String id) throws RuntimeException;
}
