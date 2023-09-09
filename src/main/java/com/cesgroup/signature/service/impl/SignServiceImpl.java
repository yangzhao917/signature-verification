package com.cesgroup.signature.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cesgroup.signature.config.SignedConfig;
import com.cesgroup.signature.config.SignedFileConfig;
import com.cesgroup.signature.config.SignedService;
import com.cesgroup.signature.entity.SignRecord;
import com.cesgroup.signature.enums.SignTypeEnum;
import com.cesgroup.signature.mapper.SignRecordMapper;
import com.cesgroup.signature.service.ISignReocrdService;
import com.cesgroup.signature.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;

/**
 * @author ：Yangzhao
 * @description：签名业务实现类
 * @date ：2023/8/30 12:06 上午
 * @version: 1.0
 */
@Slf4j
@Service
public class SignServiceImpl extends ServiceImpl<SignRecordMapper, SignRecord> implements ISignReocrdService {

    @Autowired
    private SignedConfig signedConfig;

    @Autowired
    private SignedService signedService;

    @Autowired
    private SignedFileConfig fileConfig;

    @Autowired
    private ISignReocrdService signReocrdService;

    @Override
    public void localSignature() throws Exception {
        if (!signedConfig.getEnabled()){
            throw new RuntimeException("文件签名没有启用，请先配置");
        }
        log.debug(">>>>>>>>>>>>>>>> 本地文件签名 ====> START");
        // 获取文件配置参数，过滤掉不存在的文件，只保留存在的
        List<String> fileConfigParamList = fileConfig.getFileConfigParam();
        if (CollectionUtil.isEmpty(fileConfigParamList)){
            log.debug("本地可签名文件列表为空");
            return;
        }
        // 通过文件参数配置获取所有文件，包含子目录
        List<File> allFiles = FileUtil.getAllFiles(fileConfigParamList);
        // 获取文件SHA256签名
        List<Map<String, String>> allFileHash = FileUtil.calculateAllFileHash(allFiles);
        // 对Hash进行签名
        List<Map<String, String>> hashSigned = getHashSigned(allFileHash);
        // 将计算的文件哈希保存到数据库
        this.batchSave(hashSigned);
        log.debug(">>>>>>>>>>>>>>>> 文件签名 ====> END");
    }

    /**
     * 获取Hash签名
     * @param allFileHash 文件hash
     * @return
     */
    private List<Map<String, String>> getHashSigned(List<Map<String, String>> allFileHash){
        List<Map<String, String>> signedMapList = new ArrayList<>();
        for (Map<String, String> fileHash : allFileHash) {
            for (Map.Entry<String, String> entry : fileHash.entrySet()) {
                HashMap<String, String> signedMap = new HashMap<>();
                String filePath = entry.getKey();
                String val = entry.getValue();
                String signed = signedService.sign(Base64.encode(val));
                signedMap.put(filePath, signed);
                signedMapList.add(signedMap);
            }
        }
        return signedMapList;
    }

    @Transactional
    @Override
    public void serverSignature(SignRecord signRecord) throws RuntimeException{
        log.debug("文件签名 ====> START");
        SignRecord signEntity = signReocrdService.findByFilePathAndClientIp(signRecord.getFilePath(), signRecord.getFromIp());
        // 是否存在签名记录
        if (Objects.nonNull(signEntity)){
            throw new RuntimeException(signEntity.getFilePath() + "文件已签名");
        }
        signRecord.setCreatedTime(DateUtil.date(Calendar.getInstance()));
        // 文件签名
        String signResult = signedService.sign(Base64.encode(signRecord.getSignValue()));
        signRecord.setSignValue(signResult);
        signRecord = this.signTypeConfig(signRecord);
        signReocrdService.saveOrUpdate(signRecord);
        log.debug("文件签名 ====> END");
    }

    @Transactional
    @Override
    public void batchSave(List<Map<String, String>> mapList) {
        log.debug(">>> 文件签名批量入库 ");
        List<SignRecord> signList = new ArrayList<>();
        for (Map<String, String> map : mapList) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String path = entry.getKey();
                String signature = entry.getValue();
                // 判断文件是否已经签过名
                String localhost = NetUtil.getLocalhostStr();
                SignRecord exist = this.findByFilePathAndClientIp(path, localhost);
                if (BeanUtil.isNotEmpty(exist)){
                    // 已经签过名，则更新签名时间
                    exist.setSignValue(exist.getSignValue());
                    exist.setUpdatedTime(DateUtil.date(Calendar.getInstance()));
                    signList.add(exist);
                    continue;
                }
                SignRecord sign = new SignRecord();
                sign.setSignValue(signature);
                sign.setFilePath(path);
                this.signTypeConfig(sign);
                sign.setCreatedTime(DateUtil.date(Calendar.getInstance()));
                log.debug("签名CONTENT：{}", JSONUtil.toJsonStr(sign));
                signList.add(sign);
            }
        }

        if (CollectionUtil.isNotEmpty(signList)){
            signReocrdService.saveOrUpdateBatch(signList);
            log.debug("<<< 文件签名批量入库完成，入库条数：{} ", signList.size());
        }
    }

    /**
     * 根据文件路径识别关键字配置签名类型
     * @author: yangzhao
     * @date: 2023/8/31 8:37 下午
     * @param sign 签名信息对象
     * @return com.cesgroup.signverify.entity.Sign
     */
    private SignRecord signTypeConfig(SignRecord sign){
        String filePath = sign.getFilePath();
        // 如果文件名包含rsyslog则是syslog生成的日志文件
        if (filePath.contains(SignTypeEnum.KEYWORD_RSYSLOG.getType().toLowerCase(Locale.ROOT))){
            // 获取客户端IP
            String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
            String clientIP = fileName.split("_")[0];
            sign.setFromIp(clientIP);
            sign.setType(SignTypeEnum.RSYSLOG.getType());
        }else if (filePath.contains(SignTypeEnum.KEYWORD_ETC.getType().toLowerCase(Locale.ROOT))
                || filePath.contains(SignTypeEnum.KEYWORD_VAR_LOG.getType().toLowerCase(Locale.ROOT))){
            // 如果文件名包含/etc或者/varlog,则是linux系统的文件
            sign.setType(SignTypeEnum.SYSTEM_LOG_FILE.getType());
        }else {
            // 由配置文件定义的其他文件
            sign.setType(SignTypeEnum.OUT_FILE.getType());
        }
        return sign;
    }


    @Override
    public SignRecord findByFilePathAndSignType(String path, SignTypeEnum signType) {
        QueryWrapper<SignRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .lambda()
                .eq(SignRecord::getFilePath, path)
                .eq(SignRecord::getType, signType.getType())
                .orderByDesc(SignRecord::getCreatedTime);
        return signReocrdService.getOne(queryWrapper);
    }

    @Override
    public SignRecord findByFilePathAndClientIp(String path, String clientIp) {
        log.debug("操作：根据文件路径和客户端IP查询签名记录 文件路径：{} 客户端IP：{}", path, clientIp);
        QueryWrapper<SignRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .lambda()
                .eq(SignRecord::getFilePath, path)
                .eq(SignRecord::getFromIp, clientIp)
                .orderByDesc(SignRecord::getCreatedTime);
        return signReocrdService.getOne(queryWrapper);
    }

    @Override
    public void deleteById(String id) throws RuntimeException{
        log.debug("删除签名记录，签名记录ID：{}", id);
        // 判断这个ID是否存在
        SignRecord signRecord = signReocrdService.getById(id);
        if (Objects.isNull(signRecord)){
            throw new RuntimeException("签名记录不存在");
        }
        signReocrdService.deleteById(id);
    }

}
