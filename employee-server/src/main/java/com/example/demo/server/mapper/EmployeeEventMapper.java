package com.example.demo.server.mapper;

import com.example.avro.EmployeeEvent;
import com.example.avro.State;
import com.example.demo.server.domain.Employee;
import com.example.avro.Action;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeEventMapper {

  EmployeeEvent map(Employee employee);

  @Mapping(source = "action", target = "action")
  EmployeeEvent map(Employee employee, Action action);

  Employee.State map(State state);
}
