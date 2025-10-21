package com.dzarembo.okxservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OkxServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OkxServiceApplication.class, args);
    }

}
