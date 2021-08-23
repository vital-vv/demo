package com.example.demo.processor.services.impl;

import com.example.avro.Action;
import com.example.avro.EmployeeEvent;
import com.example.avro.State;
import com.example.demo.processor.exception.UnexpectedEmployeeStateException;
import com.example.demo.processor.services.EmployeeService;
import com.example.demo.processor.services.StateMachineHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

  private final StateMachineHelper machineHelper;

  @Override
  public EmployeeEvent create(EmployeeEvent event) {
    log.debug("Create a state machine for employee: {}", event);

    StateMachine<State, Action> machine = machineHelper.createMachine(event);

    return machineHelper.saveMachine(machine);
  }

  @Override
  public EmployeeEvent manage(EmployeeEvent employeeEvent) {
    log.debug("Send en event for employee state machine: {}", employeeEvent);

    StateMachine<State, Action> machine = machineHelper.getMachine(employeeEvent);
    Message<Action> message = MessageBuilder.withPayload(employeeEvent.getAction()).build();
    Mono<Message<Action>> event = Mono.just(message);

    machine.sendEvent(event)
        .map(StateMachineEventResult::getResultType)
        .filter(result -> result == StateMachineEventResult.ResultType.DENIED)
        .doOnNext(result -> {
          throw new UnexpectedEmployeeStateException(employeeEvent);
        })
        .blockLast();

    return machineHelper.saveMachine(machine);
  }

  @Bean
  public Function<EmployeeEvent, EmployeeEvent> process() {
    return employee -> {
      if (employee.getAction() == null) {
        return create(employee);
      } else {
        return manage(employee);
      }
    };
  }
}
