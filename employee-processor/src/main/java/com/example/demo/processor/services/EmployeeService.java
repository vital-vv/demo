package com.example.demo.processor.services;

import com.example.avro.EmployeeEvent;

public interface EmployeeService {

  EmployeeEvent create(EmployeeEvent employeePayload);

  EmployeeEvent manage(EmployeeEvent employeePayload);
}
