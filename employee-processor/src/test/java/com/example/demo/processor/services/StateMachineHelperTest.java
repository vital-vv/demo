package com.example.demo.processor.services;

import com.example.avro.EmployeeAvro;
import com.example.avro.Event;
import com.example.avro.State;
import com.example.demo.processor.EmployeeAvros;
import com.example.demo.processor.StateMachineTestConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Import(StateMachineTestConfiguration.class)
class StateMachineHelperTest {

  @Autowired
  private StateMachineHelper machineHelper;

  @Test
  void testStateMachineSaving() {
    EmployeeAvro employee = EmployeeAvros.of(State.ADDED);
    StateMachine<State, Event> machine = machineHelper.createMachine(employee);

    assertNotNull(employee.getMachineId());
    Assertions.assertEquals(employee.getMachineId(), machine.getId());

    Mono<Message<Event>> event = Mono.just(MessageBuilder.withPayload(Event.TO_CHECK).build());
    StateMachineEventResult<State, Event> result = machine.sendEvent(event).blockLast();

    assertNotNull(result);
    assertEquals(StateMachineEventResult.ResultType.ACCEPTED, result.getResultType());
    Assertions.assertEquals(State.IN_CHECK, machine.getState().getId());

    employee = machineHelper.saveMachine(machine);

    assertNotNull(employee.getMachineId());
    Assertions.assertEquals(machine.getId(), employee.getMachineId());
    Assertions.assertEquals(State.IN_CHECK, employee.getState());

    machine = machineHelper.getMachine(employee);

    assertNotNull(machine);
    Assertions.assertEquals(machine.getId(), employee.getMachineId());
    Assertions.assertEquals(State.IN_CHECK, machine.getState().getId());
  }
}
