package com.example.demo.processor;

import com.example.demo.processor.configs.JpaStateMachineConfiguration;
import com.example.demo.processor.configs.StateMachineConfiguration;
import com.example.demo.processor.services.impl.StateMachineHelperImpl;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.statemachine.boot.autoconfigure.StateMachineJpaRepositoriesAutoConfiguration;
import org.springframework.test.context.ActiveProfiles;

@TestConfiguration
@ActiveProfiles("h2")
@ImportAutoConfiguration(StateMachineJpaRepositoriesAutoConfiguration.class)
@Import({StateMachineConfiguration.class, JpaStateMachineConfiguration.class, StateMachineHelperImpl.class})
public class StateMachineTestConfiguration {
}
