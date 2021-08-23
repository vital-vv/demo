package com.example.demo.server.services;

import com.example.avro.Action;
import com.example.avro.EmployeeEvent;
import com.example.demo.server.RandomObjects;
import com.example.demo.server.SchemaRegistryTestConfiguration;
import com.example.demo.server.domain.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ActiveProfiles("testcontainers")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import({TestChannelBinderConfiguration.class, SchemaRegistryTestConfiguration.class})
class EmployeeEventSourceTest {

  @Autowired
  private OutputDestination output;
  @Autowired
  private EmployeeEventSource eventSource;

  @Test
  void testSupplierBinding() {
    Employee employee = RandomObjects.employee();
    eventSource.send(employee);

    EmployeeEvent actualEvent = getFromOutput();
    assertEquals(employee.getId(), actualEvent.getId());
    assertEquals(employee.getMachineId(), actualEvent.getMachineId());
    assertNull(actualEvent.getState());
    assertNull(actualEvent.getAction());

    employee = RandomObjects.employee(Employee.State.ADDED);
    eventSource.send(employee);

    actualEvent = getFromOutput();
    assertEquals(employee.getId(), actualEvent.getId());
    assertEquals(employee.getMachineId(), actualEvent.getMachineId());
    assertEquals(employee.getState().name(), actualEvent.getState().name());
    assertNull(actualEvent.getAction());

    employee = RandomObjects.employee(Employee.State.ADDED);
    eventSource.send(employee, Action.TO_CHECK);

    actualEvent = getFromOutput();
    assertEquals(employee.getId(), actualEvent.getId());
    assertEquals(employee.getMachineId(), actualEvent.getMachineId());
    assertEquals(employee.getState().name(), actualEvent.getState().name());
    assertEquals(Action.TO_CHECK, actualEvent.getAction());
  }

  private EmployeeEvent getFromOutput() {
    Object payload = output.receive(2000, "employee-topic").getPayload();
    return (EmployeeEvent) payload;
  }
}
