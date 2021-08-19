package com.example.demo.services;

import com.example.demo.EmployeeCreatePayloads;
import com.example.demo.RandomObjects;
import com.example.demo.domain.Employee;
import com.example.demo.domain.repo.EmployeeRepository;
import com.example.demo.exception.EmployeeNotFoundException;
import com.example.demo.exception.UnexpectedEmployeeStateException;
import com.example.demo.model.EmployeeEvent;
import com.example.demo.model.EmployeeState;
import com.example.demo.model.dto.EmployeeCreatePayload;
import com.example.demo.model.dto.EmployeePayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("testcontainers")
@Transactional
@Rollback
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class EmployeeServiceTest {

  @Autowired
  private EmployeeService service;
  @Autowired
  private EmployeeRepository repo;

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
    assertEquals(EmployeeState.ADDED, employee.getState());
    assertEquals(employeePayload.getBirthDate(), employee.getBirthDate());
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
    final List<Long> ids = Stream.of(
            EmployeeCreatePayloads.of("John", "Smith"),
            EmployeeCreatePayloads.of("Jane", "Fox", Instant.now()),
            EmployeeCreatePayloads.of("Max", "Paine", Instant.now()))
        .map(service::create)
        .map(EmployeePayload::getId)
        .collect(Collectors.toList());

    for (Long id : ids) {
      service.manage(id, EmployeeEvent.TO_CHECK);
      assertEquals(EmployeeState.IN_CHECK, getEmployeeState(id));
      assertThrows(UnexpectedEmployeeStateException.class, () -> service.manage(id, EmployeeEvent.TO_CHECK));

      service.manage(id, EmployeeEvent.APPROVE);
      assertEquals(EmployeeState.APPROVED, getEmployeeState(id));
      assertThrows(UnexpectedEmployeeStateException.class, () -> service.manage(id, EmployeeEvent.TO_CHECK));

      service.manage(id, EmployeeEvent.ACTIVATE);
      assertEquals(EmployeeState.ACTIVE, getEmployeeState(id));
      assertThrows(UnexpectedEmployeeStateException.class, () -> service.manage(id, EmployeeEvent.APPROVE));
    }

    assertThrows(EmployeeNotFoundException.class, () -> service.manage(missingEmployeeId, EmployeeEvent.TO_CHECK));
  }

  private EmployeeState getEmployeeState(Long employeeId) {
    return repo.findById(employeeId)
        .map(Employee::getState)
        .orElseThrow();
  }
}
