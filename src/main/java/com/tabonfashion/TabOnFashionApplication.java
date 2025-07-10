package com.tabonfashion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.tabonfashion.entity")
public class TabOnFashionApplication {
    public static void main(String[] args) {
        SpringApplication.run(TabOnFashionApplication.class, args);
    }
}
