package com.example.demo.configs;

import com.example.demo.domain.Employee;
import com.example.demo.model.EmployeeEvent;
import com.example.demo.model.EmployeeState;
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

    StateMachineTestPlanBuilder<EmployeeState, EmployeeEvent>.StateMachineTestPlanStepBuilder plan =
        StateMachineTestPlanBuilder.<EmployeeState, EmployeeEvent>builder()
            .stateMachine(machine)
            .step()
            .expectState(EmployeeState.ADDED);

    plan.and()
        .step()
        .sendEvent(EmployeeEvent.TO_CHECK)
        .expectStateExited(EmployeeState.ADDED)
        .expectStateEntered(EmployeeState.IN_CHECK)
        .expectState(EmployeeState.IN_CHECK);

    expectEventsNotAccepted(plan, EmployeeState.IN_CHECK, EmployeeEvent.TO_CHECK, EmployeeEvent.ACTIVATE);

    plan.and()
        .step()
        .sendEvent(EmployeeEvent.APPROVE)
        .expectStateExited(EmployeeState.IN_CHECK)
        .expectStateEntered(EmployeeState.APPROVED)
        .expectState(EmployeeState.APPROVED);

    expectEventsNotAccepted(plan, EmployeeState.APPROVED, EmployeeEvent.TO_CHECK, EmployeeEvent.APPROVE);

    plan.and()
        .step()
        .sendEvent(EmployeeEvent.ACTIVATE)
        .expectStateExited(EmployeeState.APPROVED)
        .expectStateEntered(EmployeeState.ACTIVE)
        .expectState(EmployeeState.ACTIVE);

    expectEventsNotAccepted(plan, EmployeeState.ACTIVE, EmployeeEvent.values());

    plan.and()
        .build()
        .test();
  }

  private void expectEventsNotAccepted(
      StateMachineTestPlanBuilder<EmployeeState, EmployeeEvent>.StateMachineTestPlanStepBuilder plan,
      EmployeeState state, EmployeeEvent... events) {

    for (var event : events) {
      plan.and()
          .step()
          .sendEvent(event)
          .expectEventNotAccepted(1)
          .expectState(state);
    }
  }
}
