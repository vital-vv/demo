package com.example.demo.processor.exception;

import com.example.avro.EmployeeEvent;

public class UnexpectedEmployeeStateException extends RuntimeException {
  public UnexpectedEmployeeStateException(EmployeeEvent event) {
    super("Current state of the employee is not supported by the provided event: " + event);
  }
}
