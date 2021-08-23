package com.example.demo.server.services;

import com.example.avro.Action;
import com.example.demo.server.model.EmployeeCreatePayload;
import com.example.demo.server.model.EmployeePayload;

import java.util.List;

public interface EmployeeService {

  EmployeePayload create(EmployeeCreatePayload employeePayload);

  List<EmployeePayload> getAll();

  EmployeePayload getById(Long employeeId);

  void manage(Long employeeId, Action action);
}
