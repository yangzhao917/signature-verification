package com.cesgroup.signature.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 签名验签配置类
 * @author ding.haiyang
 * @since 2021/8/11
 */
@Component
public class SignedConfig {

    /** 是否开启签名 */
    @Value("${signed.enabled}")
    private Boolean enabled;
    /** 签名服务URL */
    @Value("${signed.host}")
    private String host;
    /** 签名加密方式 */
    @Value("${signed.certAlias}")
    private String certAlias;

    public Boolean getEnabled() {
        return enabled;
    }

    public SignedConfig setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host == null ? null : host.trim();
    }

    public String getCertAlias() {
        return certAlias;
    }

    public void setCertAlias(String certAlias) {
        this.certAlias = certAlias == null ? null : certAlias.trim();
    }
}
