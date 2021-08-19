package com.example.demo.services;

import com.example.demo.domain.Employee;
import com.example.demo.model.EmployeeEvent;
import com.example.demo.model.EmployeeState;
import org.springframework.statemachine.StateMachine;

public interface StateMachineHelper {

  StateMachine<EmployeeState, EmployeeEvent> createMachine(Employee employee);

  StateMachine<EmployeeState, EmployeeEvent> getMachine(Employee employee);

  Employee saveMachine(StateMachine<EmployeeState, EmployeeEvent> machine);
}
