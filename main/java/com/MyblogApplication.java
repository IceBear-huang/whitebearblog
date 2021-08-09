package com;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 47550
 */
@SpringBootApplication
@MapperScan(value = "com.mapper")
public class MyblogApplication {

    public static void main (String[] args) {
        SpringApplication.run(MyblogApplication.class, args);
    }

}
