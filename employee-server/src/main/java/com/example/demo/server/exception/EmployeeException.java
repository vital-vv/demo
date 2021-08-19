package com.example.demo.server.exception;

public abstract class EmployeeException extends RuntimeException {
  public EmployeeException(String message) {
    super(message);
  }
}
