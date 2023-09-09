package com.cesgroup.signature.service;

import com.cesgroup.signature.entity.SignRecord;
import com.cesgroup.signature.enums.SignTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ：Yangzhao
 * @description：TODO
 * @date ：2023/8/30 2:52 上午
 * @version: 1.0
 */
@Slf4j
@SpringBootTest
public class SignServiceTest {

    @Autowired
    private ISignReocrdService signReocrdService;

    /**
     * @author: yangzhao
     * @description: 测试Rsyslog文件签名
     * @date: 2023/8/30 4:43 下午
     * @param
     * @return void
     */
    @Test
    public void testSign() {
        try {
            log.debug(">>>>>>>>>>> 开始文件签名");
            long startTime = System.currentTimeMillis();
            signReocrdService.localSignature();
            long endTime = System.currentTimeMillis();
            log.debug(">>>>>>>>>>> 文件签名完成，耗时：{}ms", (endTime - startTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFindByFilePath(){
        String filePath = "/Users/yangzhao/Desktop/rsyslog-bak/172.25.75.1/172.25.75.1_2023-08-25.log";
        SignRecord sign = signReocrdService.findByFilePathAndSignType(filePath, SignTypeEnum.RSYSLOG);
        log.debug(sign.toString());
    }

}
