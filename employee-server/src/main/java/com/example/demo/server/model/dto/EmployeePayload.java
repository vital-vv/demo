package com.example.demo.server.model.dto;

import com.example.demo.server.model.EmployeeState;
import lombok.Value;
import org.springframework.lang.Nullable;

import java.time.Instant;

@Value
public class EmployeePayload {
  Long id;
  String name;
  String surname;
  @Nullable
  Instant birthDate;
  @Nullable
  String workPhone;
  @Nullable
  String personalPhone;
  @Nullable
  String contractNumber;
  EmployeeState state;
}
