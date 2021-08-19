package com.example.demo.exception;

import com.example.demo.model.EmployeeEvent;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
public class UnexpectedEmployeeStateException extends EmployeeException {
  public UnexpectedEmployeeStateException(Long employeeId, EmployeeEvent employeeEvent) {
    super("Current state of the employee is not supported by event [" + employeeEvent + "]. Employee id: " + employeeId);
  }
}
