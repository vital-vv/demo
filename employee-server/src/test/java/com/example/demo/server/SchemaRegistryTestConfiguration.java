package com.example.demo.server;

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class SchemaRegistryTestConfiguration {

  @Bean
  public SchemaRegistryClient schemaRegistryClient() {
    return new MockSchemaRegistryClient();
  }
}
