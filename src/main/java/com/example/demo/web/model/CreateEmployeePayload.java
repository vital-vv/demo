package com.example.demo.web.model;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import java.time.Instant;

@Value
public class CreateEmployeePayload {

  @NotBlank
  String name;
  @NotBlank
  String surname;
  Instant birthDate;
  String workPhone;
  String personalPhone;
  String contractNumber;

  public static CreateEmployeePayload of(String name, String surname) {
    return of(name, surname, null);
  }

  public static CreateEmployeePayload of(String name, String surname, Instant birthDate) {
    return new CreateEmployeePayload(name, surname, birthDate, null, null, null);
  }
}
