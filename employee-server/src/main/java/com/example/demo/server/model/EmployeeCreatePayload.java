package com.example.demo.server.model;

import lombok.Value;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import java.time.Instant;

@Value
public class EmployeeCreatePayload {
  @NotBlank String name;
  @NotBlank String surname;
  @Nullable
  Instant birthDate;
  @Nullable
  String workPhone;
  @Nullable
  String personalPhone;
  @Nullable
  String contractNumber;
}
