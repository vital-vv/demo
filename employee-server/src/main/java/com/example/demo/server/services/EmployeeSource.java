package com.example.demo.server.services;

import com.example.demo.server.domain.Employee;
import com.example.demo.server.model.EmployeeEvent;

public interface EmployeeSource {

  boolean sendEmployee(Employee employee);

  boolean sendEmployee(Employee employee, EmployeeEvent event);
}
