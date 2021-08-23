package com.example.demo.server.services;

import com.example.avro.Action;
import com.example.demo.server.EmployeeCreatePayloads;
import com.example.demo.server.RandomObjects;
import com.example.demo.server.configs.JpaAuditingConfiguration;
import com.example.demo.server.domain.Employee;
import com.example.demo.server.domain.repo.EmployeeRepository;
import com.example.demo.server.exception.EmployeeNotFoundException;
import com.example.demo.server.mapper.EmployeeMapperImpl;
import com.example.demo.server.model.EmployeeCreatePayload;
import com.example.demo.server.model.EmployeePayload;
import com.example.demo.server.services.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ActiveProfiles("testcontainers")
@DataJpaTest
@Import({JpaAuditingConfiguration.class, EmployeeServiceImpl.class, EmployeeMapperImpl.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeServiceTest {

  @Autowired
  private EmployeeService service;
  @Autowired
  private EmployeeRepository repo;
  @MockBean
  EmployeeEventSource employeeEventSource;

  private List<Employee> employees;

  @BeforeEach
  void setUp() {
    employees = Stream.generate(RandomObjects::employee)
        .limit(10)
        .collect(Collectors.toList());
    employees = repo.saveAll(employees);
  }

  @Test
  void testCreateEmployee() {
    final EmployeeCreatePayload employeePayload = EmployeeCreatePayloads.of("John", "Smith", Instant.now());
    final EmployeePayload employee = service.create(employeePayload);

    assertNotNull(employee.getId());
    assertEquals("John", employee.getName());
    assertEquals("Smith", employee.getSurname());
    assertNull(employee.getState());
    assertEquals(employeePayload.getBirthDate(), employee.getBirthDate());

    verify(employeeEventSource, times(1)).send(any());
  }

  @Test
  void testGetEmployeeById() {
    final Long missingEmployeeId = -1L;
    final Employee expected = employees.get(0);
    final EmployeePayload actual = service.getById(expected.getId());

    assertEquals(expected.getId(), actual.getId());
    assertEquals(expected.getName(), actual.getName());
    assertEquals(expected.getSurname(), actual.getSurname());
    assertEquals(expected.getBirthDate(), actual.getBirthDate());
    assertEquals(expected.getWorkPhone(), actual.getWorkPhone());
    assertEquals(expected.getPersonalPhone(), actual.getPersonalPhone());
    assertEquals(expected.getContractNumber(), actual.getContractNumber());
    assertEquals(expected.getState(), actual.getState());

    assertThrows(EmployeeNotFoundException.class, () -> service.getById(missingEmployeeId));
  }

  @Test
  void testGetAll() {
    assertEquals(employees.size(), service.getAll().size());
  }

  @Test
  void testEmployeeEvents() {
    final Long missingEmployeeId = -1L;
    final Long id = service.create(EmployeeCreatePayloads.of("John", "Smith"))
        .getId();

    assertThrows(EmployeeNotFoundException.class, () -> service.manage(missingEmployeeId, Action.TO_CHECK));

    for (Action action : Action.values()) {
      service.manage(id, action);
    }

    verify(employeeEventSource, times(Action.values().length)).send(any(), any());
  }
}
