package com.example.demo.server.mapper;

import com.example.avro.EmployeeAvro;
import com.example.avro.State;
import com.example.demo.server.RandomObjects;
import com.example.demo.server.domain.Employee;
import com.example.demo.server.model.EmployeeEvent;
import com.example.demo.server.model.EmployeeState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ContextConfiguration(classes = AvroMapperImpl.class)
@ExtendWith(SpringExtension.class)
class AvroMapperTest {

  @Autowired
  private AvroMapper mapper;

  @Test
  void testCreateEmployeeAvroFromEmployee() {
    final Employee expected = RandomObjects.employee(EmployeeState.ADDED);
    EmployeeAvro actual = mapper.map(expected);

    assertThat(actual.getId()).isEqualTo(expected.getId());
    assertEquals(expected.getMachineId(), actual.getMachineId());
    assertEquals(expected.getState().name(), actual.getState().name());
    assertNull(actual.getEvent());

    for (EmployeeEvent event : EmployeeEvent.values()) {
      actual = mapper.map(expected, event);

      assertThat(actual.getId()).isEqualTo(expected.getId());
      assertEquals(expected.getMachineId(), actual.getMachineId());
      assertEquals(expected.getState().name(), actual.getState().name());
      assertEquals(event.name(), actual.getEvent().name());
    }
  }

  @Test
  void testCreateEmployeeStateFromState() {
    for (State expected : State.values()) {
      final EmployeeState actual = mapper.map(expected);

      assertEquals(expected.name(), actual.name());
    }
  }
}
