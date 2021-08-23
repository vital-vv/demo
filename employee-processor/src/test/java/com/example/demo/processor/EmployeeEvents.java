package com.example.demo.processor;

import com.example.avro.Action;
import com.example.avro.EmployeeEvent;
import com.example.avro.State;

public class EmployeeEvents {

  public static EmployeeEvent empty() {
    return of(null, null);
  }

  public static EmployeeEvent of(State state) {
    return of(state, null);
  }

  public static EmployeeEvent of(State state, Action action) {
    return EmployeeEvent.newBuilder()
        .setId(1)
        .setMachineId("1")
        .setState(state)
        .setAction(action)
        .build();
  }
}
