package com.example.demo.processor.services;

import com.example.avro.Action;
import com.example.avro.EmployeeEvent;
import com.example.avro.State;
import org.springframework.statemachine.StateMachine;

public interface StateMachineHelper {

  StateMachine<State, Action> createMachine(EmployeeEvent employee);

  StateMachine<State, Action> getMachine(EmployeeEvent employee);

  EmployeeEvent saveMachine(StateMachine<State, Action> machine);
}
