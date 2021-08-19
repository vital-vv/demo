package com.example.demo.server.configs;

import com.example.avro.EmployeeAvro;
import com.example.demo.server.domain.repo.EmployeeRepository;
import com.example.demo.server.mapper.AvroMapper;
import com.example.demo.server.model.EmployeeState;
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
  public Consumer<EmployeeAvro> consumer(PlatformTransactionManager manager, EmployeeRepository repository, AvroMapper mapper) {
    return employeeAvro -> new TransactionTemplate(manager).execute(status -> {
      log.debug("Updating employee state: {}", employeeAvro);
      final EmployeeState state = mapper.map(employeeAvro.getState());
      int count = repository.setEmployeeStateById(employeeAvro.getId(), state);
      if (count != 1) {
        // TODO: update failed
        status.setRollbackOnly();
      }

      return status;
    });
  }
}
