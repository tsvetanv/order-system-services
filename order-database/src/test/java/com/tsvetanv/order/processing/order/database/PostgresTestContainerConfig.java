package com.tsvetanv.order.processing.order.database;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.PostgreSQLContainer;

@Configuration
@Profile("test") // Only loads when "test" profile is active, i.e. when using Testcontainers
public class PostgresTestContainerConfig {

  @Bean
  @ServiceConnection // Automatically bridges container to Spring DataSource
  public PostgreSQLContainer<?> postgresContainer() {
    return new PostgreSQLContainer<>("postgres:16-alpine")
      .withDatabaseName("orders")
      .withUsername("orders")
      .withPassword("orders")
      .withUrlParam("options", "-c timezone=UTC");
  }
}



