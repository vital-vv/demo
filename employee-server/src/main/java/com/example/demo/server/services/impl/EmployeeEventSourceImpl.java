package com.example.demo.server.services.impl;

import com.example.avro.Action;
import com.example.avro.EmployeeEvent;
import com.example.demo.server.domain.Employee;
import com.example.demo.server.exception.SendEmployeeEventException;
import com.example.demo.server.mapper.EmployeeEventMapper;
import com.example.demo.server.services.EmployeeEventSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class EmployeeEventSourceImpl implements EmployeeEventSource {

  private final BlockingQueue<EmployeeEvent> queue = new LinkedBlockingQueue<>();
  private final EmployeeEventMapper mapper;

  @Bean
  public Supplier<EmployeeEvent> supplier() {
    return queue::poll;
  }

  @Override
  public void send(Employee employee) {
    if (!queue.offer(mapper.map(employee))) {
      throw new SendEmployeeEventException();
    }
  }

  @Override
  public void send(Employee employee, Action action) {
    if (!queue.offer(mapper.map(employee, action))) {
      throw new SendEmployeeEventException();
    }
  }
}
