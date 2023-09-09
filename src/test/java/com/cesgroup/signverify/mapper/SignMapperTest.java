package com.cesgroup.signverify.mapper;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.cesgroup.signverify.entity.SignRecord;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author ：Yangzhao
 * @description：签名Mapper测试
 * @date ：2023/8/30 1:57 上午
 * @version: 1.0
 */
@Slf4j
@SpringBootTest
public class SignMapperTest {

    @Autowired
    private SignRecordMapper signRecordMapper;

    /**
     * @author: yangzhao
     * @description: 测试查询方法
     * @date: 2023/8/30 2:26 上午
     * @param
     * @return void
     */
    @Test
    public void testSelect(){
        List<SignRecord> signList = signRecordMapper.selectList(null);
        Assert.isTrue(1 == signList.size(), "");
        signList.forEach(System.out::println);
    }

    /**
     * @author: yangzhao
     * @description: 测试新增方法
     * @date: 2023/8/30 2:26 上午
     * @param
     * @return void
     */
    @Test
    public void testInsert(){
        SignRecord sign = new SignRecord();
        sign.setSignValue(Base64.encode("123456"));
        sign.setCreatedTime(DateTime.now());
        sign.setFilePath("c:\\111");
        sign.setType("SYS_LOG");
        sign.setFromIp("192.168.1.101");
        signRecordMapper.insert(sign);
    }

    /**
     * @author: yangzhao
     * @description: 测试删除方法
     * @date: 2023/8/30 2:27 上午
     * @param
     * @return void
     */
    @Test
    public void testDelete(){
        int delete = signRecordMapper.delete(null);
        log.debug("delete record:{}", delete);
    }

    /**
     * @author: yangzhao
     * @description: 测试修改方法
     * @date: 2023/8/30 2:27 上午
     * @param
     * @return void
     */
    @Test
    public void testUpdate(){
        List<SignRecord> signs = signRecordMapper.selectList(null);
        signs.forEach(sign -> {
            sign.setFilePath("/usr/bin/");
            sign.setUpdatedTime(DateTime.now());
            signRecordMapper.updateById(sign);
        });
    }
}
