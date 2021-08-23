package com.example.demo.processor.configs;

import com.example.avro.Action;
import com.example.avro.EmployeeEvent;
import com.example.avro.State;
import com.example.demo.processor.EmployeeEvents;
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
  private StateMachineFactory<State, Action> factory;

  @Test
  void testStateMachine() throws Exception {
    final EmployeeEvent employee = EmployeeEvents.of(State.ADDED);
    final StateMachine<State, Action> machine = factory.getStateMachine();
    machine.getExtendedState().getVariables().put("employee", employee);

    StateMachineTestPlanBuilder<State, Action>.StateMachineTestPlanStepBuilder plan =
        StateMachineTestPlanBuilder.<State, Action>builder()
            .stateMachine(machine)
            .step()
            .expectState(State.ADDED);

    plan.and()
        .step()
        .sendEvent(Action.TO_CHECK)
        .expectStateExited(State.ADDED)
        .expectStateEntered(State.IN_CHECK)
        .expectState(State.IN_CHECK);

    expectEventsNotAccepted(plan, State.IN_CHECK, Action.TO_CHECK, Action.ACTIVATE);

    plan.and()
        .step()
        .sendEvent(Action.APPROVE)
        .expectStateExited(State.IN_CHECK)
        .expectStateEntered(State.APPROVED)
        .expectState(State.APPROVED);

    expectEventsNotAccepted(plan, State.APPROVED, Action.TO_CHECK, Action.APPROVE);

    plan.and()
        .step()
        .sendEvent(Action.ACTIVATE)
        .expectStateExited(State.APPROVED)
        .expectStateEntered(State.ACTIVE)
        .expectState(State.ACTIVE);

    expectEventsNotAccepted(plan, State.ACTIVE, Action.values());

    plan.and()
        .build()
        .test();
  }

  private void expectEventsNotAccepted(
      StateMachineTestPlanBuilder<State, Action>.StateMachineTestPlanStepBuilder plan,
      State state, Action... events) {

    for (var event : events) {
      plan.and()
          .step()
          .sendEvent(event)
          .expectEventNotAccepted(1)
          .expectState(state);
    }
  }
}
