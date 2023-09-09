package com.cesgroup.signature.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ：Yangzhao
 * @description：签名表
 * @date ：2023/8/29 8:19 下午
 * @version: 1.0
 */
@ApiModel(value = "签名表实体类")
@TableName(value= "T_SIGN")
@Data
public class SignRecord implements Serializable,Cloneable{
    /** ID */
    @ApiModelProperty(name = "ID",value = "签名表记录ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id ;

    /** 文件路径 */
    @TableField(value = "FILE_PATH")
    @ApiModelProperty(name = "filePath",value = "文件路径")
    private String filePath ;

    /** 创建人 */
    @TableField(value = "CREATED_BY")
    @ApiModelProperty(name = "createdBy",value = "创建人")
    private String createdBy ;

    /** 创建时间 */
    @TableField(value = "CREATED_TIME")
    @ApiModelProperty(name = "createdTime",value = "创建时间")
    private Date createdTime ;

    /** 更新人 */
    @TableField(value = "UPDATED_BY")
    @ApiModelProperty(name = "updatedBy",value = "更新人")
    private String updatedBy ;

    /** 更新时间 */
    @TableField(value = "UPDATED_TIME")
    @ApiModelProperty(name = "updatedTime",value = "更新时间")
    private Date updatedTime ;

    /** 签名类型;SYS_LOG:系统日志,SYS_FILE:系统文件,OUT_FILE */
    @TableField(value = "TYPE")
    @ApiModelProperty(name = "type",value = "签名类型 RSYS_LOG:系统日志,SYS_FILE:系统文件,OUT_FILE")
    private String type ;

    /** 来源IP;IP地址，如：192.168.1.1 */
    @TableField(value = "FROM_IP")
    @ApiModelProperty(name = "fromIp",value = "客户端IP，如：192.168.1.1")
    private String fromIp ;

    /** 签名值;SHA256生成的值 */
    @TableField(value = "SIGN_VALUE")
    @ApiModelProperty(name = "signValue",value = "签名值")
    private String signValue ;
}