package com.example.demo.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan
public class EmployeeServerApplication {
  public static void main(String[] args) {
    SpringApplication.run(EmployeeServerApplication.class, args);
  }
}
