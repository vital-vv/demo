package com.example.demo.server;

import com.example.demo.server.domain.Employee;
import com.example.demo.server.model.EmployeeCreatePayload;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RandomObjects {

  private static final Random RANDOM = new Random();

  public static Employee employee() {
    final Employee employee = new Employee();
    employee.setId(RANDOM.nextLong());
    employee.setName(randomString(RANDOM.nextInt(10)));
    employee.setSurname(randomString(RANDOM.nextInt(10)));
    employee.setBirthDate(Instant.now());
    employee.setContractNumber(randomString(2));
    employee.setMachineId(UUID.randomUUID().toString());
    return employee;
  }

  public static Employee employee(Employee.State state) {
    final Employee employee = employee();
    employee.setState(state);
    return employee;
  }

  public static EmployeeCreatePayload employeeCreatePayload() {
    return new EmployeeCreatePayload(
        randomString(RANDOM.nextInt(10)),
        randomString(RANDOM.nextInt(10)),
        Instant.now(),
        randomString(RANDOM.nextInt(10)),
        randomString(RANDOM.nextInt(10)),
        randomString(RANDOM.nextInt(5)));
  }

  private static String randomString(int maxLength) {
    return Stream.generate(RandomObjects::nextCharacter)
        .limit(1 + RANDOM.nextInt(maxLength + 1))
        .collect(Collectors.joining(""));
  }

  private static String nextCharacter() {
    return String.valueOf((char) (RANDOM.nextInt('z' - 'a' + 1) + 'a'));
  }
}
