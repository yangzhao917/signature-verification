package com.cesgroup.signverify;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.cesgroup.signverify.mapper")
@SpringBootApplication
public class SignVerifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SignVerifyApplication.class, args);
    }

}
