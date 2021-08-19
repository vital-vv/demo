package com.example.demo.web.mapper;

import com.example.demo.emploee.domain.Employee;
import com.example.demo.web.model.CreateEmployeePayload;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

  Employee map(CreateEmployeePayload payload);
}
