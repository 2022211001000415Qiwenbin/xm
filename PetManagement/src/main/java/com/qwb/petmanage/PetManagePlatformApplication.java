package com.qwb.petmanage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.qwb.petmanage.mapper")
public class PetManagePlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(PetManagePlatformApplication.class, args);
    }
}
