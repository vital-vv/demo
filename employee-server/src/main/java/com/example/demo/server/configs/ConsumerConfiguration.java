package com.example.demo.server.configs;

import com.example.avro.EmployeeEvent;
import com.example.demo.server.domain.Employee;
import com.example.demo.server.domain.repo.EmployeeRepository;
import com.example.demo.server.exception.SetEmployeeStateException;
import com.example.demo.server.mapper.EmployeeEventMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Consumer;

@Slf4j
@Configuration
public class ConsumerConfiguration {

  @Bean
  public Consumer<EmployeeEvent> consumer(PlatformTransactionManager manager, EmployeeRepository repository, EmployeeEventMapper mapper) {
    return event -> new TransactionTemplate(manager).execute(status -> {
      log.debug("Updating employee state: {}", event);
      final Employee.State state = mapper.map(event.getState());
      int count = repository.setEmployeeStateById(event.getId(), state);
      if (count != 1) {
        log.error("Failed to update employee by event: {}", event);
        throw new SetEmployeeStateException(event);
      }

      return null;
    });
  }
}
