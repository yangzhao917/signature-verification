package com.cesgroup.signverify.dto;

/**
 * @author ding.haiyang
 * @since 2021/8/9
 */
public class GeerVerifyCertOpt {
    //为false时，不验证白名单
    private boolean allowListFlag;
    //为false时，不验证证书链
    private boolean certChainFlag;
    //为false时，不验证证书有效期
    private boolean certValidFlag;
    //为false时，不验证黑名单
    private boolean crlFlag;
    //为false时，上面四项全部不验证
    private boolean verifyCertFlag;

    public boolean isAllowListFlag() {
        return allowListFlag;
    }

    public void setAllowListFlag(boolean allowListFlag) {
        this.allowListFlag = allowListFlag;
    }

    public boolean isCertChainFlag() {
        return certChainFlag;
    }

    public void setCertChainFlag(boolean certChainFlag) {
        this.certChainFlag = certChainFlag;
    }

    public boolean isCertValidFlag() {
        return certValidFlag;
    }

    public void setCertValidFlag(boolean certValidFlag) {
        this.certValidFlag = certValidFlag;
    }

    public boolean isCrlFlag() {
        return crlFlag;
    }

    public void setCrlFlag(boolean crlFlag) {
        this.crlFlag = crlFlag;
    }

    public boolean isVerifyCertFlag() {
        return verifyCertFlag;
    }

    public void setVerifyCertFlag(boolean verifyCertFlag) {
        this.verifyCertFlag = verifyCertFlag;
    }
}
