package com.example.demo.processor.exception;

public class StateMachineCompletedException extends RuntimeException {
  public StateMachineCompletedException(String machineId) {
    super("This state machine has been completed: {}" + machineId);
  }
}
