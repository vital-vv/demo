package com.example.demo.server.configs;

import com.example.avro.EmployeeEvent;
import com.example.avro.State;
import com.example.demo.server.RandomObjects;
import com.example.demo.server.SchemaRegistryTestConfiguration;
import com.example.demo.server.domain.Employee;
import com.example.demo.server.domain.repo.EmployeeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("testcontainers")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import({TestChannelBinderConfiguration.class, SchemaRegistryTestConfiguration.class})
public class ConsumerBindingTest {

  @Autowired
  private InputDestination input;
  @Autowired
  private EmployeeRepository repository;

  private long employeeId;

  @BeforeEach
  void setUp() {
    Employee employee = repository.save(RandomObjects.employee());
    employeeId = Objects.requireNonNull(employee.getId());
  }

  @AfterEach
  void tearDown() {
    repository.deleteById(employeeId);
  }

  @Test
  void testConsumeBinding() {
    Employee.State actualState;

    input.send(new GenericMessage<>(newEmployeeEvent(State.ADDED)));

    actualState = repository.findById(employeeId).map(Employee::getState).orElseThrow();
    assertEquals(Employee.State.ADDED, actualState);

    input.send(new GenericMessage<>(newEmployeeEvent(State.IN_CHECK)));

    actualState = repository.findById(employeeId).map(Employee::getState).orElseThrow();
    assertEquals(Employee.State.IN_CHECK, actualState);

    input.send(new GenericMessage<>(newEmployeeEvent(State.APPROVED)));

    actualState = repository.findById(employeeId).map(Employee::getState).orElseThrow();
    assertEquals(Employee.State.APPROVED, actualState);

    input.send(new GenericMessage<>(newEmployeeEvent(State.ACTIVE)));

    actualState = repository.findById(employeeId).map(Employee::getState).orElseThrow();
    assertEquals(Employee.State.ACTIVE, actualState);
  }

  private EmployeeEvent newEmployeeEvent(State state) {
    return EmployeeEvent.newBuilder()
        .setId(employeeId)
        .setMachineId("1")
        .setState(state)
        .build();
  }
}
