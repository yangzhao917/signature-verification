package com.cesgroup.signature.enums;

public enum SignTypeEnum {

    /** ======================签名类型==================== */
    /** rsyslog日志文件 */
    RSYSLOG("RSYS_LOG"),
    /** 系统日志文件 */
    SYSTEM_LOG_FILE("SYS_FILE"),
    /** 外部文件 */
    OUT_FILE("OUT_FILE"),

    /* ======================关键字，用于识别签名类型===================== */
    KEYWORD_RSYSLOG("rsyslog-bak"),
    KEYWORD_ETC("/etc"),
    KEYWORD_VAR_LOG("/var/log");

    private String type;

    SignTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
