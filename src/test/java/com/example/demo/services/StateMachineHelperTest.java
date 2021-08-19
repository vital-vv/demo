package com.example.demo.services;

import com.example.demo.domain.Employee;
import com.example.demo.model.EmployeeEvent;
import com.example.demo.model.EmployeeState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("testcontainers")
@Transactional
@Rollback
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class StateMachineHelperTest {

  @Autowired
  private StateMachineHelper machineHelper;

  @Test
  void testStateMachineSaving() {
    Employee employee = new Employee();

    StateMachine<EmployeeState, EmployeeEvent> machine = machineHelper.createMachine(employee);

    assertNotNull(employee.getMachineId());
    assertEquals(employee.getMachineId(), machine.getId());

    Mono<Message<EmployeeEvent>> event = Mono.just(MessageBuilder.withPayload(EmployeeEvent.TO_CHECK).build());
    StateMachineEventResult<EmployeeState, EmployeeEvent> result = machine.sendEvent(event).blockLast();

    assertNotNull(result);
    assertEquals(StateMachineEventResult.ResultType.ACCEPTED, result.getResultType());
    assertEquals(EmployeeState.IN_CHECK, machine.getState().getId());

    employee = machineHelper.saveMachine(machine);

    assertNotNull(employee.getMachineId());
    assertEquals(machine.getId(), employee.getMachineId());
    assertEquals(EmployeeState.IN_CHECK, employee.getState());

    machine = machineHelper.getMachine(employee);

    assertNotNull(machine);
    assertEquals(machine.getId(), employee.getMachineId());
    assertEquals(EmployeeState.IN_CHECK, machine.getState().getId());
  }
}
