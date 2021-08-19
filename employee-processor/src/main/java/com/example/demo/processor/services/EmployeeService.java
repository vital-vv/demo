package com.example.demo.processor.services;

import com.example.avro.EmployeeAvro;

public interface EmployeeService {

  EmployeeAvro create(EmployeeAvro employeePayload);

  EmployeeAvro manage(EmployeeAvro employeePayload);
}
