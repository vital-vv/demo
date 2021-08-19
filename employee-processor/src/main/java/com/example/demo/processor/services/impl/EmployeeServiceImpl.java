package com.example.demo.processor.services.impl;

import com.example.avro.EmployeeAvro;
import com.example.avro.Event;
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
  public EmployeeAvro create(EmployeeAvro payload) {
    log.debug("Create a state machine for employee: {}", payload);

    StateMachine<State, Event> machine = machineHelper.createMachine(payload);

    return machineHelper.saveMachine(machine);
  }

  @Override
  public EmployeeAvro manage(EmployeeAvro payload) {
    log.debug("Send en event for employee state machine: {}", payload);

    StateMachine<State, Event> machine = machineHelper.getMachine(payload);
    Message<Event> message = MessageBuilder.withPayload(payload.getEvent()).build();
    Mono<Message<Event>> event = Mono.just(message);

    machine.sendEvent(event)
        .map(StateMachineEventResult::getResultType)
        .filter(result -> result == StateMachineEventResult.ResultType.DENIED)
        .doOnNext(result -> {
          throw new UnexpectedEmployeeStateException(payload);
        })
        .blockLast();

    return machineHelper.saveMachine(machine);
  }

  @Bean
  public Function<EmployeeAvro, EmployeeAvro> process() {
    return employee -> {
      if (employee.getEvent() == null) {
        return create(employee);
      } else {
        return manage(employee);
      }
    };
  }
}
