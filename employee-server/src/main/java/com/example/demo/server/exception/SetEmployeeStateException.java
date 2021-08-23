package com.example.demo.server.exception;

import com.example.avro.EmployeeEvent;

public class SetEmployeeStateException extends EmployeeException {
  public SetEmployeeStateException(EmployeeEvent event) {
    super("Failed to update employee state when processing the event: " + event);
  }
}
