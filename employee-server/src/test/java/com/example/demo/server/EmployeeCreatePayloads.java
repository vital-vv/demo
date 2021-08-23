package com.example.demo.server;

import com.example.demo.server.model.EmployeeCreatePayload;

import java.time.Instant;

public class EmployeeCreatePayloads {

  public static EmployeeCreatePayload of(String name, String surname) {
    return of(name, surname, null);
  }

  public static EmployeeCreatePayload of(String name, String surname, Instant birthDate) {
    return new EmployeeCreatePayload(name, surname, birthDate, null, null, null);
  }
}
