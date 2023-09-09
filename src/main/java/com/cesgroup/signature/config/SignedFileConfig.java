package com.cesgroup.signature.config;

import com.cesgroup.signature.util.FileUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 签名配置：系统文件systemfile.properties参数配置
 * @author ：Yangzhao
 * @date ：2023/8/29 9:20 下午
 */
@ConfigurationProperties(prefix = "signed")
@PropertySource("classpath:config/systemfile.properties")
@Component
@Setter
@Getter
public class SignedFileConfig {

    private List<SystemFile> systemFile;

    @Getter
    @Setter
    public static class SystemFile {
        /** 文件路径 */
        private String path;
    }

    /**
     * 获取文件配置参数，过滤掉不存在的文件，只保留存在的
     * @return
     */
    public List<String> getFileConfigParam(){
        List<SignedFileConfig.SystemFile> systemFileList = this.getSystemFile();
        return systemFileList.parallelStream()
                .map(SignedFileConfig.SystemFile::getPath)
                .filter(file -> FileUtil.isFileExists(file))
                .collect(Collectors.toList());
    }

}
