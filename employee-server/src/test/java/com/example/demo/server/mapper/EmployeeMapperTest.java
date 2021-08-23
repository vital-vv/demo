package com.example.demo.server.mapper;

import com.example.demo.server.RandomObjects;
import com.example.demo.server.domain.Employee;
import com.example.demo.server.model.EmployeeCreatePayload;
import com.example.demo.server.model.EmployeePayload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = EmployeeMapperImpl.class)
@ExtendWith(SpringExtension.class)
class EmployeeMapperTest {

  @Autowired
  private EmployeeMapper mapper;

  @Test
  void testCreateEmployeeFromEmployeeCreatePayload() {
    final EmployeeCreatePayload expected = RandomObjects.employeeCreatePayload();
    final Employee actual = mapper.map(expected);

    assertEquals(expected.getName(), actual.getName());
    assertEquals(expected.getSurname(), actual.getSurname());
    assertEquals(expected.getBirthDate(), actual.getBirthDate());
    assertEquals(expected.getWorkPhone(), actual.getWorkPhone());
    assertEquals(expected.getPersonalPhone(), actual.getPersonalPhone());
    assertEquals(expected.getContractNumber(), actual.getContractNumber());
  }

  @Test
  void testCreateEmployeePayloadFromEmployee() {
    final Employee expected = RandomObjects.employee();
    final EmployeePayload actual = mapper.map(expected);

    assertEquals(expected.getId(), actual.getId());
    assertEquals(expected.getName(), actual.getName());
    assertEquals(expected.getSurname(), actual.getSurname());
    assertEquals(expected.getBirthDate(), actual.getBirthDate());
    assertEquals(expected.getWorkPhone(), actual.getWorkPhone());
    assertEquals(expected.getPersonalPhone(), actual.getPersonalPhone());
    assertEquals(expected.getContractNumber(), actual.getContractNumber());
    assertEquals(expected.getState(), actual.getState());
  }
}
