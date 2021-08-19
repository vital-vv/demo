package com.example.demo.processor.services.impl;

import com.example.avro.EmployeeAvro;
import com.example.avro.Event;
import com.example.avro.State;
import com.example.demo.processor.exception.StateMachineCompletedException;
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

  private final StateMachineFactory<State, Event> stateMachineFactory;
  private final StateMachinePersister<State, Event, String> persister;

  @Override
  public StateMachine<State, Event> createMachine(EmployeeAvro employee) {
    StateMachine<State, Event> machine = stateMachineFactory.getStateMachine(employee.getMachineId());
    machine.getExtendedState().getVariables().put("employee", employee);
    machine.start();
    return machine;
  }

  @SneakyThrows
  @Override
  public StateMachine<State, Event> getMachine(EmployeeAvro employee) {
    String machineId = employee.getMachineId();
    StateMachine<State, Event> machine = stateMachineFactory.getStateMachine(machineId);
    machine = persister.restore(machine, machineId);
    machine.getExtendedState().getVariables().put("employee", employee);
    if (machine.isComplete()) {
      throw new StateMachineCompletedException(machineId);
    }
    machine.start();
    return machine;
  }

  @SneakyThrows
  @Transactional
  @Override
  public EmployeeAvro saveMachine(StateMachine<State, Event> machine) {
    EmployeeAvro employee = machine.getExtendedState().get("employee", EmployeeAvro.class);
    persister.persist(machine, employee.getMachineId());
    return employee;
  }
}
