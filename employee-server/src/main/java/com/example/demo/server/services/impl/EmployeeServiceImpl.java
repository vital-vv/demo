package com.example.demo.server.services.impl;

import com.example.demo.server.domain.Employee;
import com.example.demo.server.domain.repo.EmployeeRepository;
import com.example.demo.server.exception.EmployeeNotFoundException;
import com.example.demo.server.mapper.EmployeeMapper;
import com.example.demo.server.model.EmployeeEvent;
import com.example.demo.server.model.dto.EmployeeCreatePayload;
import com.example.demo.server.model.dto.EmployeePayload;
import com.example.demo.server.services.EmployeeService;
import com.example.demo.server.services.EmployeeSource;
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
  private final EmployeeSource employeeSource;

  @Override
  public EmployeePayload create(EmployeeCreatePayload employeePayload) {
    log.debug("Creating new employee: {}", employeePayload);
    Employee employee = mapper.map(employeePayload);
    employee = repository.save(employee);

    employeeSource.sendEmployee(employee);

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
  public void manage(Long employeeId, EmployeeEvent event) {
    log.debug("Updating state of the employee with id: {}. Event: {}", employeeId, event);
    Employee employee = repository.findById(employeeId)
        .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

    employeeSource.sendEmployee(employee, event);
  }
}
