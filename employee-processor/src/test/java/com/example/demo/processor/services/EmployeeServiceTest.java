package com.example.demo.processor.services;

import com.example.avro.EmployeeAvro;
import com.example.avro.Event;
import com.example.avro.State;
import com.example.demo.processor.EmployeeAvros;
import com.example.demo.processor.StateMachineTestConfiguration;
import com.example.demo.processor.exception.StateMachineCompletedException;
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
    EmployeeAvro expected = EmployeeAvros.empty();
    EmployeeAvro actual = employeeService.create(expected);

    assertEquals(expected.getId(), actual.getId());
    assertEquals(expected.getMachineId(), actual.getMachineId());
    assertEquals(State.ADDED, actual.getState());

    expected = EmployeeAvros.of(State.ADDED, Event.TO_CHECK);
    actual = employeeService.manage(expected);

    assertEquals(expected.getId(), actual.getId());
    assertEquals(expected.getMachineId(), actual.getMachineId());
    assertEquals(State.IN_CHECK, actual.getState());
    assertEquals(Event.TO_CHECK, actual.getEvent());
    assertThrowsOnEvents(expected, Event.TO_CHECK, Event.ACTIVATE);

    expected = EmployeeAvros.of(State.IN_CHECK, Event.APPROVE);
    actual = employeeService.manage(expected);

    assertEquals(expected.getId(), actual.getId());
    assertEquals(expected.getMachineId(), actual.getMachineId());
    assertEquals(State.APPROVED, actual.getState());
    assertEquals(Event.APPROVE, actual.getEvent());
    assertThrowsOnEvents(expected, Event.TO_CHECK, Event.APPROVE);

    expected = EmployeeAvros.of(State.APPROVED, Event.ACTIVATE);
    actual = employeeService.manage(expected);

    assertEquals(expected.getId(), actual.getId());
    assertEquals(expected.getMachineId(), actual.getMachineId());
    assertEquals(State.ACTIVE, actual.getState());
    assertEquals(Event.ACTIVATE, actual.getEvent());
    assertThrowsOnEvents(expected, StateMachineCompletedException.class, Event.TO_CHECK, Event.APPROVE, Event.ACTIVATE);
  }

  private void assertThrowsOnEvents(EmployeeAvro avro, Event... events) {
    assertThrowsOnEvents(avro, UnexpectedEmployeeStateException.class, events);
  }

  private void assertThrowsOnEvents(EmployeeAvro avro, Class<? extends Throwable> expectedType, Event... events) {
    for (Event event : events) {
      EmployeeAvro expected = EmployeeAvros.of(avro.getState(), event);
      assertThrows(expectedType, () -> employeeService.manage(expected));
    }
  }
}
