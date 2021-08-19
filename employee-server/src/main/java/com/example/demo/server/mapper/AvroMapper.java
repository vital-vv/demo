package com.example.demo.server.mapper;

import com.example.avro.EmployeeAvro;
import com.example.avro.State;
import com.example.demo.server.domain.Employee;
import com.example.demo.server.model.EmployeeEvent;
import com.example.demo.server.model.EmployeeState;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AvroMapper {

  EmployeeAvro map(Employee employee);

  @Mapping(source = "event", target = "event")
  EmployeeAvro map(Employee employee, EmployeeEvent event);

  EmployeeState map(State state);
}
