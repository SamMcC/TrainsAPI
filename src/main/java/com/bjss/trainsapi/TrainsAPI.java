package com.bjss.trainsapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ServletComponentScan
@SpringBootApplication(scanBasePackages = "com.bjss.trainsapi")
@EnableJpaRepositories("com.bjss.trainsapi.model.repository")
@EntityScan("com.bjss.trainsapi.model.persistence")
public class TrainsAPI {
    public static void main(String[] args) {
        SpringApplication.run(TrainsAPI.class, args);
    }
}
