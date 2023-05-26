package com.example.tina;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@MapperScan("com.example.tina.mapper")
@ServletComponentScan
public class TinaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TinaApplication.class, args);
    }

}
