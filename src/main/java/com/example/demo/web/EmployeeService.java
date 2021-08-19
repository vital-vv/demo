package com.example.demo.web;

import com.example.demo.emploee.EmployeeRepository;
import com.example.demo.emploee.domain.Employee;
import com.example.demo.emploee.model.EmployeeEvent;
import com.example.demo.emploee.model.EmployeeState;
import com.example.demo.exception.EmployeeNotFoundException;
import com.example.demo.exception.UnexpectedEmployeeStateException;
import com.example.demo.sm.StateMachineHelper;
import com.example.demo.web.mapper.EmployeeMapper;
import com.example.demo.web.model.CreateEmployeePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeService {

  private final EmployeeMapper mapper;
  private final StateMachineHelper machineHelper;
  private final EmployeeRepository repository;

  public Employee create(CreateEmployeePayload employeePayload) {
    log.debug("Creating new employee: {}", employeePayload);
    Employee employee = mapper.map(employeePayload);
    StateMachine<EmployeeState, EmployeeEvent> machine = machineHelper.createMachine(employee);

    return repository.save(machineHelper.saveMachine(machine));
  }

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
