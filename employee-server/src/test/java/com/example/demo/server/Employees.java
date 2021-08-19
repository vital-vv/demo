package com.example.demo.server;

import com.example.demo.server.domain.Employee;

public class Employees {

  public static Employee of(String name) {
    return of(name, null);
  }

  public static Employee of(String name, String surname) {
    return of(name, surname, null);
  }

  public static Employee of(String name, String surname, String machineId) {
    final Employee employee = new Employee();
    employee.setName(name);
    employee.setSurname(surname);
    employee.setMachineId(machineId);
    return employee;
  }
}
