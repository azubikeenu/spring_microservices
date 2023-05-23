package com.azubike.ellipsis;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class InventoryFailover {
    public static void main(String[] args) {
        SpringApplication.run(InventoryFailover.class ,args);
    }
}
