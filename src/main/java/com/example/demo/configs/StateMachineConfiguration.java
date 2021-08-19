package com.example.demo.configs;

import com.example.demo.domain.Employee;
import com.example.demo.model.EmployeeEvent;
import com.example.demo.model.EmployeeState;
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
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@Slf4j
@Configuration
@EnableStateMachineFactory
public class StateMachineConfiguration extends EnumStateMachineConfigurerAdapter<EmployeeState, EmployeeEvent> {

  @Override
  public void configure(StateMachineConfigurationConfigurer<EmployeeState, EmployeeEvent> config) throws Exception {
    config.withConfiguration()
        .autoStartup(true)
        .listener(listener());
  }

  @Override
  public void configure(StateMachineStateConfigurer<EmployeeState, EmployeeEvent> states) throws Exception {
    states.withStates()
        .initial(EmployeeState.ADDED)
        .end(EmployeeState.ACTIVE)
        .states(EnumSet.allOf(EmployeeState.class));
  }

  @Override
  public void configure(StateMachineTransitionConfigurer<EmployeeState, EmployeeEvent> transitions) throws Exception {
    transitions
        .withExternal()
        .source(EmployeeState.ADDED)
        .target(EmployeeState.IN_CHECK)
        .event(EmployeeEvent.TO_CHECK)
        .guard(toCheckGuard())
        .action(toCheckAction())

        .and()
        .withExternal()
        .source(EmployeeState.IN_CHECK)
        .target(EmployeeState.APPROVED)
        .event(EmployeeEvent.APPROVE)
        .guard(approveGuard())
        .action(approveAction())

        .and()
        .withExternal()
        .source(EmployeeState.APPROVED)
        .target(EmployeeState.ACTIVE)
        .event(EmployeeEvent.ACTIVATE)
        .guard(activateGuard())
        .action(activateAction());
  }

  @Bean
  public Guard<EmployeeState, EmployeeEvent> toCheckGuard() {
    return context -> {
      Employee employee = context.getExtendedState().get("employee", Employee.class);
      return employee.getState() == EmployeeState.ADDED;
    };
  }

  @Bean
  public Action<EmployeeState, EmployeeEvent> toCheckAction() {
    return context -> {
      Employee employee = context.getExtendedState().get("employee", Employee.class);
      employee.setState(EmployeeState.IN_CHECK);
    };
  }

  @Bean
  public Guard<EmployeeState, EmployeeEvent> approveGuard() {
    return context -> {
      Employee employee = context.getExtendedState().get("employee", Employee.class);
      return employee.getState() == EmployeeState.IN_CHECK;
    };
  }

  @Bean
  public Action<EmployeeState, EmployeeEvent> approveAction() {
    return context -> {
      Employee employee = context.getExtendedState().get("employee", Employee.class);
      employee.setState(EmployeeState.APPROVED);
    };
  }

  @Bean
  public Guard<EmployeeState, EmployeeEvent> activateGuard() {
    return context -> {
      Employee employee = context.getExtendedState().get("employee", Employee.class);
      return employee.getState() == EmployeeState.APPROVED;
    };
  }

  @Bean
  public Action<EmployeeState, EmployeeEvent> activateAction() {
    return context -> {
      Employee employee = context.getExtendedState().get("employee", Employee.class);
      employee.setState(EmployeeState.ACTIVE);
    };
  }

  @Bean
  public StateMachineListener<EmployeeState, EmployeeEvent> listener() {
    return new StateMachineListenerAdapter<>() {
      @Override
      public void stateChanged(State<EmployeeState, EmployeeEvent> from, State<EmployeeState, EmployeeEvent> to) {
        log.debug("Statemachine state has been changed from {} to {}", from, to);
      }
    };
  }
}
