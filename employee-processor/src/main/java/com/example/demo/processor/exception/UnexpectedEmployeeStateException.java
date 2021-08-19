package com.example.demo.processor.exception;

import com.example.avro.EmployeeAvro;

public class UnexpectedEmployeeStateException extends RuntimeException {
  public UnexpectedEmployeeStateException(EmployeeAvro employeeAvro) {
    super("Current state of the employee is not supported by the provided event: " + employeeAvro);
  }
}
