package com.example.demo.processor.configs;

import com.example.avro.EmployeeAvro;
import com.example.avro.Event;
import com.example.avro.State;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;

import java.util.EnumSet;

@Slf4j
@Configuration
@EnableStateMachineFactory
public class StateMachineConfiguration extends EnumStateMachineConfigurerAdapter<State, Event> {

  @Override
  public void configure(StateMachineConfigurationConfigurer<State, Event> config) throws Exception {
    config.withConfiguration()
        .listener(listener());
  }

  @Override
  public void configure(StateMachineStateConfigurer<State, Event> states) throws Exception {
    states.withStates()
        .initial(State.ADDED, addedAction())
        .end(State.ACTIVE)
        .states(EnumSet.allOf(State.class));
  }

  @Override
  public void configure(StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
    transitions
        .withExternal()
        .source(State.ADDED)
        .target(State.IN_CHECK)
        .event(Event.TO_CHECK)
        .guard(toCheckGuard())
        .action(toCheckAction())

        .and()
        .withExternal()
        .source(State.IN_CHECK)
        .target(State.APPROVED)
        .event(Event.APPROVE)
        .guard(approveGuard())
        .action(approveAction())

        .and()
        .withExternal()
        .source(State.APPROVED)
        .target(State.ACTIVE)
        .event(Event.ACTIVATE)
        .guard(activateGuard())
        .action(activateAction());
  }

  @Bean
  public Action<State, Event> addedAction() {
    return context -> {
      EmployeeAvro employee = context.getExtendedState().get("employee", EmployeeAvro.class);
      employee.setState(State.ADDED);
    };
  }

  @Bean
  public Guard<State, Event> toCheckGuard() {
    return context -> {
      EmployeeAvro employee = context.getExtendedState().get("employee", EmployeeAvro.class);
      return employee.getState() == State.ADDED;
    };
  }

  @Bean
  public Action<State, Event> toCheckAction() {
    return context -> {
      EmployeeAvro employee = context.getExtendedState().get("employee", EmployeeAvro.class);
      employee.setState(State.IN_CHECK);
    };
  }

  @Bean
  public Guard<State, Event> approveGuard() {
    return context -> {
      EmployeeAvro employee = context.getExtendedState().get("employee", EmployeeAvro.class);
      return employee.getState() == State.IN_CHECK;
    };
  }

  @Bean
  public Action<State, Event> approveAction() {
    return context -> {
      EmployeeAvro employee = context.getExtendedState().get("employee", EmployeeAvro.class);
      employee.setState(State.APPROVED);
    };
  }

  @Bean
  public Guard<State, Event> activateGuard() {
    return context -> {
      EmployeeAvro employee = context.getExtendedState().get("employee", EmployeeAvro.class);
      return employee.getState() == State.APPROVED;
    };
  }

  @Bean
  public Action<State, Event> activateAction() {
    return context -> {
      EmployeeAvro employee = context.getExtendedState().get("employee", EmployeeAvro.class);
      employee.setState(State.ACTIVE);
    };
  }

  @Bean
  public StateMachineListener<State, Event> listener() {
    return new StateMachineListenerAdapter<>() {
      @Override
      public void stateChanged(org.springframework.statemachine.state.State<State, Event> from,
                               org.springframework.statemachine.state.State<State, Event> to) {
        log.debug("Statemachine state has been changed from {} to {}", from, to);
      }
    };
  }
}
