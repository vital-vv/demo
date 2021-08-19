package com.example.demo.server.mapper;

import com.example.demo.server.domain.Employee;
import com.example.demo.server.model.dto.EmployeeCreatePayload;
import com.example.demo.server.model.dto.EmployeePayload;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

  Employee map(EmployeeCreatePayload payload);

  EmployeePayload map(Employee employee);
}
