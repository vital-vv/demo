package com.example.demo.services.impl;

import com.example.demo.domain.Employee;
import com.example.demo.domain.repo.EmployeeRepository;
import com.example.demo.exception.EmployeeNotFoundException;
import com.example.demo.exception.UnexpectedEmployeeStateException;
import com.example.demo.mapper.EmployeeMapper;
import com.example.demo.model.EmployeeEvent;
import com.example.demo.model.EmployeeState;
import com.example.demo.model.dto.EmployeeCreatePayload;
import com.example.demo.model.dto.EmployeePayload;
import com.example.demo.services.EmployeeService;
import com.example.demo.services.StateMachineHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

  private final EmployeeMapper mapper;
  private final StateMachineHelper machineHelper;
  private final EmployeeRepository repository;

  @Override
  public EmployeePayload create(EmployeeCreatePayload employeePayload) {
    log.debug("Creating new employee: {}", employeePayload);
    Employee employee = mapper.map(employeePayload);
    StateMachine<EmployeeState, EmployeeEvent> machine = machineHelper.createMachine(employee);
    employee = repository.save(machineHelper.saveMachine(machine));

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
  public void manage(Long employeeId, EmployeeEvent employeeEvent) {
    log.debug("Updating state of the employee with id: {}. Event: {}", employeeId, employeeEvent);
    StateMachine<EmployeeState, EmployeeEvent> machine = repository.findById(employeeId)
        .map(machineHelper::getMachine)
        .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
    Message<EmployeeEvent> message = MessageBuilder.withPayload(employeeEvent).build();
    Mono<Message<EmployeeEvent>> event = Mono.just(message);

    machine.sendEvent(event)
        .map(StateMachineEventResult::getResultType)
        .filter(result -> result == StateMachineEventResult.ResultType.DENIED)
        .doOnNext(result -> {
          throw new UnexpectedEmployeeStateException(employeeId, employeeEvent);
        })
        .blockLast();

    repository.save(machineHelper.saveMachine(machine));
  }
}
