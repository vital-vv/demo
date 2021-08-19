package com.example.demo.services.impl;

import com.example.demo.domain.Employee;
import com.example.demo.model.EmployeeEvent;
import com.example.demo.model.EmployeeState;
import com.example.demo.services.StateMachineHelper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StateMachineHelperImpl implements StateMachineHelper {

  private final StateMachineFactory<EmployeeState, EmployeeEvent> stateMachineFactory;
  private final StateMachinePersister<EmployeeState, EmployeeEvent, String> persister;

  @Override
  public StateMachine<EmployeeState, EmployeeEvent> createMachine(Employee employee) {
    String machineId = UUID.randomUUID().toString();
    employee.setMachineId(machineId);
    StateMachine<EmployeeState, EmployeeEvent> machine = stateMachineFactory.getStateMachine(machineId);
    machine.getExtendedState().getVariables().put("employee", employee);
    return machine;
  }

  @SneakyThrows
  @Override
  public StateMachine<EmployeeState, EmployeeEvent> getMachine(Employee employee) {
    String machineId = employee.getMachineId();
    StateMachine<EmployeeState, EmployeeEvent> machine = stateMachineFactory.getStateMachine(machineId);
    machine = persister.restore(machine, machineId);
    machine.getExtendedState().getVariables().put("employee", employee);
    return machine;
  }

  @SneakyThrows
  @Transactional(propagation = Propagation.MANDATORY)
  @Override
  public Employee saveMachine(StateMachine<EmployeeState, EmployeeEvent> machine) {
    Employee employee = machine.getExtendedState().get("employee", Employee.class);
    persister.persist(machine, employee.getMachineId());
    return employee;
  }
}
