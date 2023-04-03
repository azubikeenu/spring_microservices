package com.azubike.msscbeerservice;

import com.azubike.msscbeerservice.services.inventory.BeerInventoryServiceRestTemplateImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class MsscBeerServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(MsscBeerServiceApplication.class, args);

  }
}
