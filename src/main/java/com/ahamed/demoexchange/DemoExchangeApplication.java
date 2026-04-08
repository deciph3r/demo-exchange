package com.ahamed.demoexchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DemoExchangeApplication {

    static void main(String[] args) {
        SpringApplication.run(DemoExchangeApplication.class, args);
    }

}
