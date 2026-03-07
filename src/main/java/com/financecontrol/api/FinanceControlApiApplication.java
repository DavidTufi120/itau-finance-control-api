package com.financecontrol.api;

import com.financecontrol.api.infrastructure.config.SecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SecurityProperties.class)
public class FinanceControlApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanceControlApiApplication.class, args);
    }
}
