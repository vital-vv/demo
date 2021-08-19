package com.example.demo.web;

import com.example.demo.emploee.EmployeeRepository;
import com.example.demo.emploee.domain.Employee;
import com.example.demo.emploee.model.EmployeeEvent;
import com.example.demo.emploee.model.EmployeeState;
import com.example.demo.exception.EmployeeNotFoundException;
import com.example.demo.exception.UnexpectedEmployeeStateException;
import com.example.demo.web.model.CreateEmployeePayload;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("testcontainers")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class EmployeeServiceTest {

  @Autowired
  private EmployeeService service;
  @Autowired
  private EmployeeRepository repo;

  @Test
  void testCreateEmployee() {
    final CreateEmployeePayload employeePayload = CreateEmployeePayload.of("John", "Smith", Instant.now());
    final Employee employee = service.create(employeePayload);

    assertNotNull(employee.getId());
    assertNotNull(employee.getVersion());
    assertNotNull(employee.getCreatedOn());
    assertNotNull(employee.getUpdatedOn());
    assertEquals("John", employee.getName());
    assertEquals("Smith", employee.getSurname());
    assertEquals(EmployeeState.ADDED, employee.getState());
    assertEquals(employeePayload.getBirthDate(), employee.getBirthDate());
  }

  @Test
  void testEmployeeEvents() {
    final List<Long> ids = Stream.of(
            CreateEmployeePayload.of("John", "Smith"),
            CreateEmployeePayload.of("Jane", "Fox", Instant.now()),
            CreateEmployeePayload.of("Max", "Paine", Instant.now()))
        .map(service::create)
        .map(Employee::getId)
        .collect(Collectors.toList());
    final Long missingEmployeeId = -1L;

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
