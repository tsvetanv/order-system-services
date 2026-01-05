package com.tsvetanv.order.processing.order.database;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;


@TestConfiguration(proxyBeanMethods = false)
public class PostgresTestContainerConfig {

  @Bean
  @ServiceConnection // Automates spring.datasource properties
  public PostgreSQLContainer<?> postgresContainer() {
    return new PostgreSQLContainer<>("postgres:16-alpine")
      .withDatabaseName("orders")
      .withUsername("orders")
      .withPassword("orders")
      .withUrlParam("options", "-c timezone=UTC");
    // Note: container.start() is handled automatically by Spring Boot/Testcontainers here
  }
}


