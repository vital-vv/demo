package com.example.demo.processor.services;

import com.example.avro.Action;
import com.example.avro.EmployeeEvent;
import com.example.avro.State;
import com.example.demo.processor.EmployeeEvents;
import com.example.demo.processor.StateMachineTestConfiguration;
import com.example.demo.processor.exception.StateMachineCompletedException;
import com.example.demo.processor.exception.StateMachineNotFound;
import com.example.demo.processor.exception.UnexpectedEmployeeStateException;
import com.example.demo.processor.services.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({StateMachineTestConfiguration.class, EmployeeServiceImpl.class})
class EmployeeServiceTest {

  @Autowired
  private EmployeeService employeeService;

  @Test
  void testEmployeeEvents() {
    assertThrows(StateMachineNotFound.class, () -> employeeService.manage(EmployeeEvents.empty()));

    EmployeeEvent expected = EmployeeEvents.empty();
    EmployeeEvent actual = employeeService.create(expected);

    assertEquals(expected.getId(), actual.getId());
    assertEquals(expected.getMachineId(), actual.getMachineId());
    assertEquals(State.ADDED, actual.getState());

    expected = EmployeeEvents.of(State.ADDED, Action.TO_CHECK);
    actual = employeeService.manage(expected);

    assertEquals(expected.getId(), actual.getId());
    assertEquals(expected.getMachineId(), actual.getMachineId());
    assertEquals(State.IN_CHECK, actual.getState());
    assertEquals(Action.TO_CHECK, actual.getAction());
    assertThrowsOnEvents(expected, Action.TO_CHECK, Action.ACTIVATE);

    expected = EmployeeEvents.of(State.IN_CHECK, Action.APPROVE);
    actual = employeeService.manage(expected);

    assertEquals(expected.getId(), actual.getId());
    assertEquals(expected.getMachineId(), actual.getMachineId());
    assertEquals(State.APPROVED, actual.getState());
    assertEquals(Action.APPROVE, actual.getAction());
    assertThrowsOnEvents(expected, Action.TO_CHECK, Action.APPROVE);

    expected = EmployeeEvents.of(State.APPROVED, Action.ACTIVATE);
    actual = employeeService.manage(expected);

    assertEquals(expected.getId(), actual.getId());
    assertEquals(expected.getMachineId(), actual.getMachineId());
    assertEquals(State.ACTIVE, actual.getState());
    assertEquals(Action.ACTIVATE, actual.getAction());
    assertThrowsOnEvents(expected, StateMachineCompletedException.class, Action.TO_CHECK, Action.APPROVE, Action.ACTIVATE);
  }

  private void assertThrowsOnEvents(EmployeeEvent avro, Action... events) {
    assertThrowsOnEvents(avro, UnexpectedEmployeeStateException.class, events);
  }

  private void assertThrowsOnEvents(EmployeeEvent avro, Class<? extends Throwable> expectedType, Action... actions) {
    for (Action action : actions) {
      EmployeeEvent expected = EmployeeEvents.of(avro.getState(), action);
      assertThrows(expectedType, () -> employeeService.manage(expected));
    }
  }
}
