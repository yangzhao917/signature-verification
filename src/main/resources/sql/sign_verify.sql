-- 签名表
CREATE TABLE "SIGN_VERIFY"."T_SIGN"
(
    "ID" VARCHAR2(32) NOT NULL,
    "FILE_PATH" VARCHAR2(8188),
    "CREATED_BY" VARCHAR2(32),
    "CREATED_TIME" TIMESTAMP(6),
    "UPDATED_BY" VARCHAR2(32),
    "UPDATED_TIME" TIMESTAMP(6),
    "TYPE" VARCHAR2(50),
    "FROM_IP" VARCHAR2(50),
    "SIGN_VALUE" VARCHAR2(4000),
    NOT CLUSTER PRIMARY KEY("ID")) STORAGE(ON "MAIN", CLUSTERBTR) ;

COMMENT ON TABLE T_SIGN IS '签名表';
COMMENT ON COLUMN T_SIGN.ID IS 'ID';
COMMENT ON COLUMN T_SIGN.FILE_PATH IS '乐观锁';
COMMENT ON COLUMN T_SIGN.CREATED_BY IS '创建人';
COMMENT ON COLUMN T_SIGN.CREATED_TIME IS '创建时间';
COMMENT ON COLUMN T_SIGN.UPDATED_BY IS '更新人';
COMMENT ON COLUMN T_SIGN.UPDATED_TIME IS '更新时间';
COMMENT ON COLUMN T_SIGN.TYPE IS '签名类型;SYS_LOG:系统日志,SYS_FILE:系统文件,OUT_FILE';
COMMENT ON COLUMN T_SIGN.FROM_IP IS '来源IP;IP地址，如：192.168.1.1';
COMMENT ON COLUMN T_SIGN.SIGN_VALUE IS '签名内容';

-- 验签记录表
CREATE TABLE "SIGN_VERIFY"."T_SIGN_VERIFY_RECORD"
(
    "ID" VARCHAR2(32),
    "CREATED_BY" VARCHAR2(32),
    "CREATED_TIME" TIMESTAMP(6),
    "UPDATED_BY" VARCHAR2(32),
    "UPDATED_TIME" TIMESTAMP(6),
    "SIGN_VALUE" VARCHAR2(4000),
    "VERIFY_STATUS" VARCHAR2(50)) STORAGE(ON "MAIN", CLUSTERBTR) ;

COMMENT ON TABLE "SIGN_VERIFY".T_SIGN_VERIFY_RECORD IS '验签表';
COMMENT ON COLUMN "SIGN_VERIFY".T_SIGN_VERIFY_RECORD.ID IS 'ID;签名表，签名ID';
COMMENT ON COLUMN "SIGN_VERIFY".T_SIGN_VERIFY_RECORD.CREATED_BY IS '创建人';
COMMENT ON COLUMN "SIGN_VERIFY".T_SIGN_VERIFY_RECORD.CREATED_TIME IS '创建时间';
COMMENT ON COLUMN "SIGN_VERIFY".T_SIGN_VERIFY_RECORD.UPDATED_BY IS '更新人';
COMMENT ON COLUMN "SIGN_VERIFY".T_SIGN_VERIFY_RECORD.UPDATED_TIME IS '更新时间';
COMMENT ON COLUMN "SIGN_VERIFY".T_SIGN_VERIFY_RECORD.SIGN_VALUE IS '签名内容';
COMMENT ON COLUMN "SIGN_VERIFY".T_SIGN_VERIFY_RECORD.VERIFY_STATUS IS '验签状态;验签状态：PASS:通过,NO_PASS:不通过';
