package com.example.demo.processor.services;

import com.example.avro.Action;
import com.example.avro.EmployeeEvent;
import com.example.avro.State;
import com.example.demo.processor.EmployeeEvents;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("h2")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(TestChannelBinderConfiguration.class)
public class ProcessBindingTest {

  @Autowired
  private InputDestination input;
  @Autowired
  private OutputDestination output;

  @DisplayName("Updating an employee state machine by events from the employee-topic")
  @Test
  void testProcessBinding() {
    EmployeeEvent event = EmployeeEvents.of(State.ADDED);
    EmployeeEvent actual = sendEvent(event);

    assertEquals(event, actual);

    event = EmployeeEvents.of(State.ADDED, Action.TO_CHECK);
    actual = sendEvent(event);

    assertEquals(event.getId(), actual.getId());
    assertEquals(State.IN_CHECK, actual.getState());

    event = EmployeeEvents.of(State.IN_CHECK, Action.APPROVE);
    actual = sendEvent(event);

    assertEquals(event.getId(), actual.getId());
    assertEquals(State.APPROVED, actual.getState());

    event = EmployeeEvents.of(State.APPROVED, Action.ACTIVATE);
    actual = sendEvent(event);

    assertEquals(event.getId(), actual.getId());
    assertEquals(State.ACTIVE, actual.getState());
  }

  private EmployeeEvent sendEvent(EmployeeEvent event) {
    input.send(new GenericMessage<>(event), "employee-topic");
    Object payload = output.receive(2000, "update-employee-topic").getPayload();
    return (EmployeeEvent) payload;
  }
}
