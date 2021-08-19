package com.example.demo.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class EmployeeNotFoundException extends EmployeeException {
  public EmployeeNotFoundException(Long employeeId) {
    super("Employee not found by id: " + employeeId);
  }
}
