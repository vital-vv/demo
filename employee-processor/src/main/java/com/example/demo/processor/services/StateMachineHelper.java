package com.example.demo.processor.services;

import com.example.avro.EmployeeAvro;
import com.example.avro.Event;
import com.example.avro.State;
import org.springframework.statemachine.StateMachine;

public interface StateMachineHelper {

  StateMachine<State, Event> createMachine(EmployeeAvro employee);

  StateMachine<State, Event> getMachine(EmployeeAvro employee);

  EmployeeAvro saveMachine(StateMachine<State, Event> machine);
}
