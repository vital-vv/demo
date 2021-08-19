package com.example.demo.processor;

import com.example.avro.EmployeeAvro;
import com.example.avro.Event;
import com.example.avro.State;

public class EmployeeAvros {

  public static EmployeeAvro empty() {
    return of(null, null);
  }

  public static EmployeeAvro of(State state) {
    return of(state, null);
  }

  public static EmployeeAvro of(State state, Event event) {
    return EmployeeAvro.newBuilder()
        .setId(1)
        .setMachineId("1")
        .setState(state)
        .setEvent(event)
        .build();
  }
}
