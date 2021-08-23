package com.example.demo.server.mapper;

import com.example.avro.Action;
import com.example.avro.EmployeeEvent;
import com.example.avro.State;
import com.example.demo.server.RandomObjects;
import com.example.demo.server.domain.Employee;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ContextConfiguration(classes = EmployeeEventMapperImpl.class)
@ExtendWith(SpringExtension.class)
class EmployeeEventMapperTest {

  @Autowired
  private EmployeeEventMapper mapper;

  @Test
  void testCreateEmployeeAvroFromEmployee() {
    final Employee expected = RandomObjects.employee(Employee.State.ADDED);
    EmployeeEvent actual = mapper.map(expected);

    assertThat(actual.getId()).isEqualTo(expected.getId());
    assertEquals(expected.getMachineId(), actual.getMachineId());
    assertEquals(expected.getState().name(), actual.getState().name());
    assertNull(actual.getAction());

    for (Action action : Action.values()) {
      actual = mapper.map(expected, action);

      assertThat(actual.getId()).isEqualTo(expected.getId());
      assertEquals(expected.getMachineId(), actual.getMachineId());
      assertEquals(expected.getState().name(), actual.getState().name());
      assertEquals(action.name(), actual.getAction().name());
    }
  }

  @Test
  void testCreateEmployeeStateFromState() {
    for (State expected : State.values()) {
      final Employee.State actual = mapper.map(expected);

      assertEquals(expected.name(), actual.name());
    }
  }
}
