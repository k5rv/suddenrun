package com.ksaraev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class SuddenrunApplication {
  public static void main(String[] args) {
    SpringApplication.run(SuddenrunApplication.class, args);
  }
}
