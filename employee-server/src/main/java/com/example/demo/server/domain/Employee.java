package com.example.demo.server.domain;

import com.example.demo.server.model.EmployeeState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends AbstractAuditable {

  @NotBlank
  @Column(nullable = false)
  private String name;
  @NotBlank
  @Column(nullable = false)
  private String surname;

  private String workPhone;
  private String personalPhone;
  private Instant birthDate;

  private String contractNumber;

  @Enumerated(EnumType.STRING)
  private EmployeeState state;

  @NotNull
  @Column(nullable = false, updatable = false)
  private String machineId = UUID.randomUUID().toString();
}
