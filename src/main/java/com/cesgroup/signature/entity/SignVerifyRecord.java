package com.cesgroup.signature.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ：Yangzhao
 * @description：验签记录表
 * @date ：2023/8/29 8:36 下午
 * @version: 1.0
 */

@ApiModel(value = "验签记录表实体类")
@TableName(value="T_SIGN_VERIFY_RECORD")
@Getter
@Setter
public class SignVerifyRecord implements Serializable,Cloneable{
    /** ID;签名表，签名ID */
    @TableField(value = "ID")
    @ApiModelProperty(name = "id",value = "签名表，签名ID")
    private String id ;

    /** 创建人 */
    @TableField(value = "CREATED_BY")
    @ApiModelProperty(name = "createdBy",value = "创建人")
    private String createdBy ;

    /** 创建时间 */
    @TableField(value = "CREATED_TIME")
    @NotNull("签名创建时间不能为空")
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

    /** 签名值;SHA256生成的值 */
    @TableField(value = "SIGN_VALUE")
    @ApiModelProperty(name = "signValue",value = "签名值：SHA256生成的值")
    private String signValue ;

    /** 验签状态;验签状态：PASS:通过,NO_PASS:不通过 */
    @TableField(value = "VERIFY_STATUS")
    @ApiModelProperty(name = "verifyStatus",value = "验签状态：PASS:通过,NO_PASS:不通过")
    private String verifyStatus ;
}