package com.example.demo.server.services.impl;

import com.example.avro.Action;
import com.example.demo.server.domain.Employee;
import com.example.demo.server.domain.repo.EmployeeRepository;
import com.example.demo.server.exception.EmployeeNotFoundException;
import com.example.demo.server.mapper.EmployeeMapper;
import com.example.demo.server.model.EmployeeCreatePayload;
import com.example.demo.server.model.EmployeePayload;
import com.example.demo.server.services.EmployeeEventSource;
import com.example.demo.server.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

  private final EmployeeMapper mapper;
  private final EmployeeRepository repository;
  private final EmployeeEventSource employeeEventSource;

  @Override
  public EmployeePayload create(EmployeeCreatePayload employeePayload) {
    log.debug("Creating new employee: {}", employeePayload);
    Employee employee = mapper.map(employeePayload);
    employee = repository.save(employee);

    employeeEventSource.send(employee);

    return mapper.map(employee);
  }

  @Override
  public List<EmployeePayload> getAll() {
    log.debug("Get all existing employees");

    return repository.findAll().stream()
        .map(mapper::map)
        .collect(Collectors.toList());
  }

  @Override
  public EmployeePayload getById(Long employeeId) {
    log.debug("Getting employee bi identifier: {}", employeeId);

    return repository.findById(employeeId)
        .map(mapper::map)
        .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
  }

  @Override
  public void manage(Long employeeId, Action action) {
    log.debug("Updating state of the employee with id: {}. Event: {}", employeeId, action);
    Employee employee = repository.findById(employeeId)
        .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

    employeeEventSource.send(employee, action);
  }
}
