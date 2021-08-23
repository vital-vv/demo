package com.example.demo.server.services;

import com.example.avro.Action;
import com.example.demo.server.domain.Employee;

public interface EmployeeEventSource {

  void send(Employee employee);

  void send(Employee employee, Action action);
}
