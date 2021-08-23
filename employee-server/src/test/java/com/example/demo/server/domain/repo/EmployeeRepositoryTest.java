package com.example.demo.server.domain.repo;

import com.example.demo.server.Employees;
import com.example.demo.server.RandomObjects;
import com.example.demo.server.configs.JpaAuditingConfiguration;
import com.example.demo.server.domain.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("testcontainers")
@DataJpaTest
@Import(JpaAuditingConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeRepositoryTest {

  @Autowired
  private EmployeeRepository repository;

  @Test
  void testEmployeeRepository() {
    Employee expectEmployee = RandomObjects.employee();
    Employee actualEmployee = repository.save(expectEmployee);

    assertEquals(1, repository.count());
    assertNull(actualEmployee.getState());

    assertSysFieldsNotNull(actualEmployee);

    assertEquals(expectEmployee.getName(), actualEmployee.getName());
    assertEquals(expectEmployee.getSurname(), actualEmployee.getSurname());
    assertEquals(expectEmployee.getBirthDate(), actualEmployee.getBirthDate());
    assertEquals(expectEmployee.getContractNumber(), actualEmployee.getContractNumber());
    assertEquals(expectEmployee.getMachineId(), actualEmployee.getMachineId());

    expectEmployee = Employees.of("Sam", "Fisher", "1");
    actualEmployee = repository.save(expectEmployee);

    assertEquals(2, repository.count());
    assertNull(actualEmployee.getState());

    assertSysFieldsNotNull(actualEmployee);

    assertEquals(expectEmployee.getName(), actualEmployee.getName());
    assertEquals(expectEmployee.getSurname(), actualEmployee.getSurname());
    assertEquals(expectEmployee.getMachineId(), actualEmployee.getMachineId());

    assertDoesNotThrow(() -> repository.deleteAll());
  }

  @Test
  void testSetEmployeeStateById() {
    final Employee.State expected = Employee.State.IN_CHECK;
    final Long missingEmployeeId = -1L;

    int count = repository.setEmployeeStateById(missingEmployeeId, expected);
    assertEquals(0, count);

    final Employee employee = repository.save(RandomObjects.employee());
    final Long employeeId = employee.getId();
    assertNotNull(employeeId);
    assertEquals(1, repository.setEmployeeStateById(employeeId, expected));

    assertDoesNotThrow(() -> repository.deleteAll());
  }

  private void assertSysFieldsNotNull(Employee employee) {
    assertNotNull(employee.getId());
    assertNotNull(employee.getVersion());
    assertNotNull(employee.getCreatedOn());
    assertNotNull(employee.getUpdatedOn());
  }
}
