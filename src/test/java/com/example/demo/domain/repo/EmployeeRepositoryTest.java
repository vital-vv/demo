package com.example.demo.domain.repo;

import com.example.demo.Employees;
import com.example.demo.RandomObjects;
import com.example.demo.configs.JpaAuditingConfiguration;
import com.example.demo.domain.Employee;
import com.example.demo.model.EmployeeState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    assertEquals(EmployeeState.ADDED, actualEmployee.getState());

    assertSysFieldsNotNull(actualEmployee);

    assertEquals(expectEmployee.getName(), actualEmployee.getName());
    assertEquals(expectEmployee.getSurname(), actualEmployee.getSurname());
    assertEquals(expectEmployee.getBirthDate(), actualEmployee.getBirthDate());
    assertEquals(expectEmployee.getContractNumber(), actualEmployee.getContractNumber());
    assertEquals(expectEmployee.getMachineId(), actualEmployee.getMachineId());

    expectEmployee = Employees.of("Sam", "Fisher", "1");
    actualEmployee = repository.save(expectEmployee);

    assertEquals(2, repository.count());
    assertEquals(EmployeeState.ADDED, actualEmployee.getState());

    assertSysFieldsNotNull(actualEmployee);

    assertEquals(expectEmployee.getName(), actualEmployee.getName());
    assertEquals(expectEmployee.getSurname(), actualEmployee.getSurname());
    assertEquals(expectEmployee.getMachineId(), actualEmployee.getMachineId());
  }

  private void assertSysFieldsNotNull(Employee employee) {
    assertNotNull(employee.getId());
    assertNotNull(employee.getVersion());
    assertNotNull(employee.getCreatedOn());
    assertNotNull(employee.getUpdatedOn());
  }
}
