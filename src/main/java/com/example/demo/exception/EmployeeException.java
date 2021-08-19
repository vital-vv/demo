package com.example.demo.exception;

public abstract class EmployeeException extends RuntimeException {
  public EmployeeException(String message) {
    super(message);
  }
}
