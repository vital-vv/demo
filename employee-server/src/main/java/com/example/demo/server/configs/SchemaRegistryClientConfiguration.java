package com.example.demo.server.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.schema.registry.client.ConfluentSchemaRegistryClient;
import org.springframework.cloud.schema.registry.client.SchemaRegistryClient;
import org.springframework.cloud.schema.registry.client.config.SchemaRegistryClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SchemaRegistryClientProperties.class)
public class SchemaRegistryClientConfiguration {

  @ConditionalOnMissingBean(io.confluent.kafka.schemaregistry.client.SchemaRegistryClient.class)
  @Bean
  public SchemaRegistryClient schemaRegistryClient(SchemaRegistryClientProperties properties,
                                                   RestTemplateBuilder restTemplateBuilder,
                                                   ObjectMapper mapper) {
    final ConfluentSchemaRegistryClient client = new ConfluentSchemaRegistryClient(restTemplateBuilder.build(), mapper);
    client.setEndpoint(properties.getEndpoint());
    return client;
  }
}
