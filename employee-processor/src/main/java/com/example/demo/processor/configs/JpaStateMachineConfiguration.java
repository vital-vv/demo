package com.example.demo.processor.configs;

import com.example.avro.Action;
import com.example.avro.State;
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
  public StateMachinePersister<State, Action, String> stateMachinePersister(
      JpaStateMachineRepository jpaStateMachineRepository) {
    return new DefaultStateMachinePersister<>(stateMachineRuntimePersister(jpaStateMachineRepository));
  }

  @Bean
  public StateMachineRuntimePersister<State, Action, String> stateMachineRuntimePersister(
      JpaStateMachineRepository jpaStateMachineRepository) {
    return new JpaPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
  }
}
