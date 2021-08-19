package com.example.demo.server.services;

import com.example.demo.server.model.EmployeeEvent;
import com.example.demo.server.model.dto.EmployeeCreatePayload;
import com.example.demo.server.model.dto.EmployeePayload;

import java.util.List;

public interface EmployeeService {

  EmployeePayload create(EmployeeCreatePayload employeePayload);

  List<EmployeePayload> getAll();

  EmployeePayload getById(Long employeeId);

  void manage(Long employeeId, EmployeeEvent event);
}
