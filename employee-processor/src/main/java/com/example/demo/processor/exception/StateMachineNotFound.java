package com.example.demo.processor.exception;

import com.example.avro.EmployeeEvent;

public class StateMachineNotFound extends RuntimeException {
  public StateMachineNotFound(EmployeeEvent event) {
    super("This state machine not found: {}" + event);
  }
}
