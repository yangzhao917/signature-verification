package com.cesgroup.signature.enums;

public enum SignStatusEnum {

    /** 签名成功 */
    PASS("PASS"),
    /** 系统日志文件 */
    NO_PASS("NO_PASS");

    private String type;

    SignStatusEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
