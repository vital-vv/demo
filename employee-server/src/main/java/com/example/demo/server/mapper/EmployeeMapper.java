package com.example.demo.server.mapper;

import com.example.demo.server.domain.Employee;
import com.example.demo.server.model.EmployeeCreatePayload;
import com.example.demo.server.model.EmployeePayload;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

  Employee map(EmployeeCreatePayload payload);

  EmployeePayload map(Employee employee);
}
