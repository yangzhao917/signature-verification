package com.cesgroup.signverify.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cesgroup.signverify.entity.SignRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: yangzhao
 * @description: 签名表的增删改查
 * @date: 2023/8/29 8:41 下午
 * @return
 */
@Mapper
public interface SignRecordMapper extends BaseMapper<SignRecord> {

}
