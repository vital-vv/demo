package com.example.demo.config;

import com.example.demo.emploee.model.EmployeeEvent;
import com.example.demo.emploee.model.EmployeeState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;

@Configuration
public class JpaStateMachineConfiguration {

  @Bean
  public StateMachinePersister<EmployeeState, EmployeeEvent, String> stateMachinePersister(
      JpaStateMachineRepository jpaStateMachineRepository) {
    return new DefaultStateMachinePersister<>(stateMachineRuntimePersister(jpaStateMachineRepository));
  }

  @Bean
  public StateMachineRuntimePersister<EmployeeState, EmployeeEvent, String> stateMachineRuntimePersister(
      JpaStateMachineRepository jpaStateMachineRepository) {
    return new JpaPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
  }
}
