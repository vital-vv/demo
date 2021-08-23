package com.example.demo.processor.services;

import com.example.avro.Action;
import com.example.avro.EmployeeEvent;
import com.example.avro.State;
import com.example.demo.processor.EmployeeEvents;
import com.example.demo.processor.StateMachineTestConfiguration;
import com.example.demo.processor.exception.StateMachineCompletedException;
import com.example.demo.processor.exception.StateMachineNotFound;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(StateMachineTestConfiguration.class)
class StateMachineHelperTest {

  @Autowired
  private StateMachineHelper machineHelper;

  @Test
  void testStateMachineSave() {
    EmployeeEvent employee = EmployeeEvents.of(State.ADDED);
    StateMachine<State, Action> machine = machineHelper.createMachine(employee);

    assertNotNull(employee.getMachineId());
    Assertions.assertEquals(employee.getMachineId(), machine.getId());

    Mono<Message<Action>> event = Mono.just(MessageBuilder.withPayload(Action.TO_CHECK).build());
    StateMachineEventResult<State, Action> result = machine.sendEvent(event).blockLast();

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

  @Test
  void testStateMachineRestore() {
    final EmployeeEvent employee = EmployeeEvents.of(State.ADDED);

    assertThrows(StateMachineNotFound.class, () -> machineHelper.getMachine(employee));

    StateMachine<State, Action> machine = machineHelper.createMachine(employee);
    machineHelper.saveMachine(machine);
    assertDoesNotThrow(() -> machineHelper.getMachine(employee));

    StateMachineContext<State, Action> context = new DefaultStateMachineContext<>(State.ACTIVE, null, null, null, null, employee.getMachineId());
    machine.getStateMachineAccessor().doWithRegion(sma -> sma.resetStateMachine(context));
    machine.getExtendedState().getVariables().put("employee", employee);
    machineHelper.saveMachine(machine);

    assertThrows(StateMachineCompletedException.class, () -> machineHelper.getMachine(employee));
  }
}
