package com.example.demo.emploee.domain;

import com.example.demo.emploee.model.EmployeeState;
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

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends BaseEntity {

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

  @NotNull
  @Column(nullable = false, columnDefinition = "varchar(255) default 'ADDED'")
  @Enumerated(EnumType.STRING)
  private EmployeeState state = EmployeeState.ADDED;

  @NotNull
  @Column(nullable = false, updatable = false)
  private String machineId;
}
