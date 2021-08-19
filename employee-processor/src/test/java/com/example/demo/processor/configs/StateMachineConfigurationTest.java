package com.example.demo.processor.configs;

import com.example.avro.EmployeeAvro;
import com.example.avro.Event;
import com.example.avro.State;
import com.example.demo.processor.EmployeeAvros;
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
  private StateMachineFactory<State, Event> factory;

  @Test
  void testStateMachine() throws Exception {
    final EmployeeAvro employee = EmployeeAvros.of(State.ADDED);
    final StateMachine<State, Event> machine = factory.getStateMachine();
    machine.getExtendedState().getVariables().put("employee", employee);

    StateMachineTestPlanBuilder<State, Event>.StateMachineTestPlanStepBuilder plan =
        StateMachineTestPlanBuilder.<State, Event>builder()
            .stateMachine(machine)
            .step()
            .expectState(State.ADDED);

    plan.and()
        .step()
        .sendEvent(Event.TO_CHECK)
        .expectStateExited(State.ADDED)
        .expectStateEntered(State.IN_CHECK)
        .expectState(State.IN_CHECK);

    expectEventsNotAccepted(plan, State.IN_CHECK, Event.TO_CHECK, Event.ACTIVATE);

    plan.and()
        .step()
        .sendEvent(Event.APPROVE)
        .expectStateExited(State.IN_CHECK)
        .expectStateEntered(State.APPROVED)
        .expectState(State.APPROVED);

    expectEventsNotAccepted(plan, State.APPROVED, Event.TO_CHECK, Event.APPROVE);

    plan.and()
        .step()
        .sendEvent(Event.ACTIVATE)
        .expectStateExited(State.APPROVED)
        .expectStateEntered(State.ACTIVE)
        .expectState(State.ACTIVE);

    expectEventsNotAccepted(plan, State.ACTIVE, Event.values());

    plan.and()
        .build()
        .test();
  }

  private void expectEventsNotAccepted(
      StateMachineTestPlanBuilder<State, Event>.StateMachineTestPlanStepBuilder plan,
      State state, Event... events) {

    for (var event : events) {
      plan.and()
          .step()
          .sendEvent(event)
          .expectEventNotAccepted(1)
          .expectState(state);
    }
  }
}
