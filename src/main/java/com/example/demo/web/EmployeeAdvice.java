package com.example.demo.web;

import com.example.demo.exception.EmployeeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Optional;

@Slf4j
@ControllerAdvice
public class EmployeeAdvice extends ResponseEntityExceptionHandler {

  @ExceptionHandler
  public ResponseEntity<Object> onException(Exception exception, WebRequest request) {
    log.error("Failed to process request", exception);
    return handleExceptionInternal(exception, null, new HttpHeaders(), getStatus(exception), request);
  }

  @ExceptionHandler
  public ResponseEntity<Object> onException(EmployeeException exception, WebRequest request) {
    log.warn("Failed to process request", exception);
    return handleExceptionInternal(exception, null, new HttpHeaders(), getStatus(exception), request);
  }

  private HttpStatus getStatus(Exception exception) {
    return Optional.ofNullable(exception.getClass().getAnnotation(ResponseStatus.class))
        .map(ResponseStatus::code)
        .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
