package com.example.demo.services;

import com.example.demo.model.EmployeeEvent;
import com.example.demo.model.dto.EmployeeCreatePayload;
import com.example.demo.model.dto.EmployeePayload;

import java.util.List;

public interface EmployeeService {

  EmployeePayload create(EmployeeCreatePayload employeePayload);

  List<EmployeePayload> getAll();

  EmployeePayload getById(Long employeeId);

  void manage(Long employeeId, EmployeeEvent employeeEvent);
}
