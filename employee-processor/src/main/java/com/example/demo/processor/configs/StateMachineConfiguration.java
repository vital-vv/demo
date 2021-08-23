package com.example.demo.processor.configs;

import com.example.avro.EmployeeEvent;
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
public class StateMachineConfiguration extends EnumStateMachineConfigurerAdapter<State, com.example.avro.Action> {

  @Override
  public void configure(StateMachineConfigurationConfigurer<State, com.example.avro.Action> config) throws Exception {
    config.withConfiguration()
        .listener(listener());
  }

  @Override
  public void configure(StateMachineStateConfigurer<State, com.example.avro.Action> states) throws Exception {
    states.withStates()
        .initial(State.ADDED, addedAction())
        .end(State.ACTIVE)
        .states(EnumSet.allOf(State.class));
  }

  @Override
  public void configure(StateMachineTransitionConfigurer<State, com.example.avro.Action> transitions) throws Exception {
    transitions
        .withExternal()
        .source(State.ADDED)
        .target(State.IN_CHECK)
        .event(com.example.avro.Action.TO_CHECK)
        .guard(toCheckGuard())
        .action(toCheckAction())

        .and()
        .withExternal()
        .source(State.IN_CHECK)
        .target(State.APPROVED)
        .event(com.example.avro.Action.APPROVE)
        .guard(approveGuard())
        .action(approveAction())

        .and()
        .withExternal()
        .source(State.APPROVED)
        .target(State.ACTIVE)
        .event(com.example.avro.Action.ACTIVATE)
        .guard(activateGuard())
        .action(activateAction());
  }

  @Bean
  public Action<State, com.example.avro.Action> addedAction() {
    return context -> {
      EmployeeEvent employee = context.getExtendedState().get("employee", EmployeeEvent.class);
      employee.setState(State.ADDED);
    };
  }

  @Bean
  public Guard<State, com.example.avro.Action> toCheckGuard() {
    return context -> {
      EmployeeEvent employee = context.getExtendedState().get("employee", EmployeeEvent.class);
      return employee.getState() == State.ADDED;
    };
  }

  @Bean
  public Action<State, com.example.avro.Action> toCheckAction() {
    return context -> {
      EmployeeEvent employee = context.getExtendedState().get("employee", EmployeeEvent.class);
      employee.setState(State.IN_CHECK);
    };
  }

  @Bean
  public Guard<State, com.example.avro.Action> approveGuard() {
    return context -> {
      EmployeeEvent employee = context.getExtendedState().get("employee", EmployeeEvent.class);
      return employee.getState() == State.IN_CHECK;
    };
  }

  @Bean
  public Action<State, com.example.avro.Action> approveAction() {
    return context -> {
      EmployeeEvent employee = context.getExtendedState().get("employee", EmployeeEvent.class);
      employee.setState(State.APPROVED);
    };
  }

  @Bean
  public Guard<State, com.example.avro.Action> activateGuard() {
    return context -> {
      EmployeeEvent employee = context.getExtendedState().get("employee", EmployeeEvent.class);
      return employee.getState() == State.APPROVED;
    };
  }

  @Bean
  public Action<State, com.example.avro.Action> activateAction() {
    return context -> {
      EmployeeEvent employee = context.getExtendedState().get("employee", EmployeeEvent.class);
      employee.setState(State.ACTIVE);
    };
  }

  @Bean
  public StateMachineListener<State, com.example.avro.Action> listener() {
    return new StateMachineListenerAdapter<>() {
      @Override
      public void stateChanged(org.springframework.statemachine.state.State<State, com.example.avro.Action> from,
                               org.springframework.statemachine.state.State<State, com.example.avro.Action> to) {
        log.debug("Statemachine state has been changed from {} to {}", from, to);
      }
    };
  }
}
