package com.example.demo.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.TOO_MANY_REQUESTS)
public class SendEmployeeEventException extends EmployeeException {
  public SendEmployeeEventException() {
    super("Failed to send employee event");
  }
}
