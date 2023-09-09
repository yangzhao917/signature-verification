package com.cesgroup.signature.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cesgroup.signature.entity.SignRecord;
import com.cesgroup.signature.entity.SignVerifyRecord;

import java.util.List;

/**
 * @author: yangzhao
 * @description: 验签业务接口，用于处理验签的业务逻辑
 * @date: 2023/8/29 9:01 下午
 */
public interface ISignVerifyRecordService extends IService<SignVerifyRecord> {

    /**
     * 本地文件签名验证，将验签记录保存到数据库
     * @author: yangzhao
     * @date: 2023/8/29 9:03 下午
     * @return
     */
    void signVerify() throws Exception;

    /**
     * 文件签名验证，将验签记录保存到数据库
     * @author: yangzhao
     * @date: 2023/8/29 9:03 下午
     * @return
     */
    void signVerify(SignRecord sign) throws Exception;

    /**
     * 根据签名记录ID查询验签记录
     * @param id 签名记录ID
     * @return 签名记录
     */
    List<SignVerifyRecord> listById(String id);
}
