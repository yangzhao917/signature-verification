package com.cesgroup.signverify.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.net.NetUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cesgroup.signverify.config.SignedConfig;
import com.cesgroup.signverify.config.SignedFileConfig;
import com.cesgroup.signverify.config.SignedService;
import com.cesgroup.signverify.entity.SignRecord;
import com.cesgroup.signverify.entity.SignVerifyRecord;
import com.cesgroup.signverify.enums.SignStatusEnum;
import com.cesgroup.signverify.mapper.SignVerifyRecordMapper;
import com.cesgroup.signverify.service.ISignReocrdService;
import com.cesgroup.signverify.service.ISignVerifyRecordService;
import com.cesgroup.signverify.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author ：Yangzhao
 * @description：签名验证实现类
 * @date ：2023/8/30 12:07 上午
 * @version: 1.0
 */
@Slf4j
@Service
public class SignVerifyServiceImpl extends ServiceImpl<SignVerifyRecordMapper, SignVerifyRecord> implements ISignVerifyRecordService {

    @Autowired
    private SignedFileConfig fileConfig;

    @Resource
    private ISignReocrdService signService;

    @Autowired
    private SignedService signedService;

    @Autowired
    private ISignVerifyRecordService signVerifyRecordService;

    @Autowired
    private SignedConfig signedConfig;

    @Transactional
    @Override
    public void signVerify() throws Exception {
        if (!signedConfig.getEnabled()){
            throw new RuntimeException("文件签名没有启用，请先配置");
        }

        // 获取文件配置参数，过滤掉不存在的文件，只保留存在的
        List<String> fileConfigParamList = fileConfig.getFileConfigParam();
        if (CollectionUtil.isEmpty(fileConfigParamList)){
            return;
        }
        // 获取验签成功后的签名记录
        List<File> allFiles = FileUtil.getAllFiles(fileConfigParamList);

        // 获取所有文件签名
        List<SignVerifyRecord> allFileSign = this.getSignedRecordAfterVerifyingSignature(allFiles);
        // 保存文件签名
        if (CollectionUtil.isNotEmpty(allFileSign)){
            signVerifyRecordService.saveBatch(allFileSign);
        }
    }

    /**
     * 获取验签成功后的签名记录
     * @author: yangzhao
     * @date: 2023/9/1 1:31 上午
     * @param allFiles
     * @return java.util.List<com.cesgroup.signverify.entity.SignVerifyRecord>
     */
    private List<SignVerifyRecord> getSignedRecordAfterVerifyingSignature(List<File> allFiles) throws IOException, NoSuchAlgorithmException {
        // 存储验签成功后的签名记录
        List<SignVerifyRecord> verifyPassList = new ArrayList<>();

        // 对本地文件进行SHA256签名
        Map<String, String> fileHash = FileUtil.getFileHash(allFiles);
        for (Map.Entry<String, String> entry : fileHash.entrySet()) {
            String path = entry.getKey();
            String currentHash = entry.getValue();

            // 根据文件路径和ip查询有没有相关的签名记录，如果存在则进行验签操作
            String localhost = NetUtil.getLocalhostStr();
            SignRecord sign = signService.findByFilePathAndClientIp(path, localhost);
            if (BeanUtil.isEmpty(sign)){
                continue;
            }
            // 调用外部接口，进行验签处理，验签成功后保存验签记录到验签记录表
            boolean verify = signedService.verifySigned(Base64.encode(currentHash), sign.getSignValue());
            SignVerifyRecord signVerifyRecord = new SignVerifyRecord();
            BeanUtils.copyProperties(sign, signVerifyRecord);
            if (verify){
                signVerifyRecord.setVerifyStatus(SignStatusEnum.PASS.getType());
            }else {
                signVerifyRecord.setVerifyStatus(SignStatusEnum.NO_PASS.getType());
            }
            // 验签创建时间
            signVerifyRecord.setCreatedTime(DateUtil.date(Calendar.getInstance()));
            verifyPassList.add(signVerifyRecord);
        }
        return verifyPassList;
    }

    @Override
    public void signVerify(SignRecord signVO){
        if (!signedConfig.getEnabled()){
            throw new RuntimeException("文件签名未启用 signed.enabled:" + signedConfig.getEnabled());
        }
        // 判断签名记录在签名表中是否存在
        SignRecord signEntity = signService.findByFilePathAndClientIp(signVO.getFilePath(), signVO.getFromIp());
        // 存在签名记录
        if (Objects.nonNull(signEntity)) {
            // 对文件签名信息进行验签，签名表存在签名记录并且新签名和签名表中记录一致则验签成功
            String encode = Base64.encode(signVO.getSignValue());
            String signValue = signEntity.getSignValue();
            log.debug("签名原文:{},签名消息:{}", encode, signValue);
            boolean result = signedService.verifySigned(encode, signValue);
            log.debug("验签响应内容：{}", result);

            SignVerifyRecord signVerifyRecord = new SignVerifyRecord();
            signVerifyRecord.setId(signEntity.getId());
            signVerifyRecord.setSignValue(signEntity.getSignValue());
            signVerifyRecord.setCreatedTime(DateUtil.date(Calendar.getInstance()));
            // 验签通过
            if (result) {
                signVerifyRecord.setVerifyStatus(SignStatusEnum.PASS.getType());
            } else {
                signVerifyRecord.setVerifyStatus(SignStatusEnum.NO_PASS.getType());
            }
            // 保存签名记录
            signVerifyRecordService.save(signVerifyRecord);
        }else {
            throw new RuntimeException("文件没有签名");
        }

    }

    @Override
    public List<SignVerifyRecord> listById(String id) {
        QueryWrapper<SignVerifyRecord> signVerifyRecord = new QueryWrapper<>();
        LambdaQueryWrapper<SignVerifyRecord> wrapper = signVerifyRecord.lambda()
                .eq(SignVerifyRecord::getId, id)
                .orderByDesc(SignVerifyRecord::getCreatedTime);
        return signVerifyRecordService.list(wrapper);
    }
}
