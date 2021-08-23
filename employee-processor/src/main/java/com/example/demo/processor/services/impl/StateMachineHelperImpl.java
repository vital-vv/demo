package com.example.demo.processor.services.impl;

import com.example.avro.Action;
import com.example.avro.EmployeeEvent;
import com.example.avro.State;
import com.example.demo.processor.exception.StateMachineCompletedException;
import com.example.demo.processor.exception.StateMachineNotFound;
import com.example.demo.processor.services.StateMachineHelper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class StateMachineHelperImpl implements StateMachineHelper {

  private final StateMachineFactory<State, Action> stateMachineFactory;
  private final StateMachinePersister<State, Action, String> persister;

  @Override
  public StateMachine<State, Action> createMachine(EmployeeEvent employee) {
    StateMachine<State, Action> machine = stateMachineFactory.getStateMachine(employee.getMachineId());
    machine.getExtendedState().getVariables().put("employee", employee);
    machine.start();
    return machine;
  }

  @SneakyThrows
  @Override
  public StateMachine<State, Action> getMachine(EmployeeEvent employee) {
    String machineId = employee.getMachineId();
    StateMachine<State, Action> machine = stateMachineFactory.getStateMachine(machineId);
    machine = persister.restore(machine, machineId);
    machine.getExtendedState().getVariables().put("employee", employee);
    if (machine.getId() == null) {
      throw new StateMachineNotFound(employee);
    }
    if (machine.isComplete()) {
      throw new StateMachineCompletedException(machineId);
    }
    machine.start();
    return machine;
  }

  @SneakyThrows
  @Transactional
  @Override
  public EmployeeEvent saveMachine(StateMachine<State, Action> machine) {
    EmployeeEvent employee = machine.getExtendedState().get("employee", EmployeeEvent.class);
    persister.persist(machine, employee.getMachineId());
    return employee;
  }
}
