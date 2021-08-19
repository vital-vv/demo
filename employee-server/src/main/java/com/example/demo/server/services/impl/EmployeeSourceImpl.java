package com.example.demo.server.services.impl;

import com.example.avro.EmployeeAvro;
import com.example.demo.server.domain.Employee;
import com.example.demo.server.mapper.AvroMapper;
import com.example.demo.server.model.EmployeeEvent;
import com.example.demo.server.services.EmployeeSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class EmployeeSourceImpl implements EmployeeSource {

  private final BlockingQueue<EmployeeAvro> queue = new LinkedBlockingQueue<>();
  private final AvroMapper mapper;

  @Bean
  public Supplier<EmployeeAvro> supplier() {
    return queue::poll;
  }

  @Override
  public boolean sendEmployee(Employee employee) {
    return queue.offer(mapper.map(employee));
  }

  @Override
  public boolean sendEmployee(Employee employee, EmployeeEvent event) {
    return queue.offer(mapper.map(employee, event));
  }
}
