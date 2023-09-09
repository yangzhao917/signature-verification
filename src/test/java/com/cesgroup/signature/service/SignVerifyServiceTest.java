package com.cesgroup.signature.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ：Yangzhao
 * @description：TODO
 * @date ：2023/9/1 1:53 上午
 * @version: 1.0
 */
@Slf4j
@SpringBootTest
public class SignVerifyServiceTest {

    @Autowired
    ISignVerifyRecordService signVerifyRecordService;

    @Test
    public void testSignVerify(){
        log.debug(">>>>>>>>>>>>> 文件验签 START");
        long startTime = System.currentTimeMillis();
        try {
            signVerifyRecordService.signVerify();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        log.debug(">>>>>>>>>>>>> 文件验签 END,耗时：{}ms", (endTime - startTime));
    }
}
