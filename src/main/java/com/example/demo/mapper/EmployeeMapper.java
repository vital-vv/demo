package com.example.demo.mapper;

import com.example.demo.domain.Employee;
import com.example.demo.model.dto.EmployeeCreatePayload;
import com.example.demo.model.dto.EmployeePayload;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

  Employee map(EmployeeCreatePayload payload);

  EmployeePayload map(Employee employee);
}
