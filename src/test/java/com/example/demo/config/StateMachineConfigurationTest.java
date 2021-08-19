package com.example.demo.config;

import com.example.demo.emploee.domain.Employee;
import com.example.demo.emploee.model.EmployeeEvent;
import com.example.demo.emploee.model.EmployeeState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.test.StateMachineTestPlanBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = StateMachineConfiguration.class)
@ExtendWith(SpringExtension.class)
class StateMachineConfigurationTest {

  @Autowired
  private StateMachineFactory<EmployeeState, EmployeeEvent> factory;

  @Test
  void testStateMachine() throws Exception {
    final Employee employee = new Employee();
    final StateMachine<EmployeeState, EmployeeEvent> machine = factory.getStateMachine();
    machine.getExtendedState().getVariables().put("employee", employee);

    StateMachineTestPlanBuilder.<EmployeeState, EmployeeEvent>builder()
        .stateMachine(machine)
        .step()
        .expectState(EmployeeState.ADDED)

        .and()
        .step()
        .sendEvent(EmployeeEvent.TO_CHECK)
        .expectEventNotAccepted(0)
        .expectStateExited(EmployeeState.ADDED)
        .expectStateEntered(EmployeeState.IN_CHECK)
        .expectState(EmployeeState.IN_CHECK)

        .and()
        .step()
        .sendEvent(EmployeeEvent.TO_CHECK)
        .expectEventNotAccepted(1)
        .expectState(EmployeeState.IN_CHECK)

        .and()
        .step()
        .sendEvent(EmployeeEvent.APPROVE)
        .expectStateExited(EmployeeState.IN_CHECK)
        .expectStateEntered(EmployeeState.APPROVED)
        .expectState(EmployeeState.APPROVED)

        .and()
        .step()
        .sendEvent(EmployeeEvent.ACTIVATE)
        .expectStateExited(EmployeeState.APPROVED)
        .expectStateEntered(EmployeeState.ACTIVE)
        .expectState(EmployeeState.ACTIVE)

        .and()
        .build()
        .test();
  }
}
