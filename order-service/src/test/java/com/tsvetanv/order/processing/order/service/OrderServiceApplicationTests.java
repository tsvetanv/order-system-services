package com.tsvetanv.order.processing.order.service;

//import com.tsvetanv.order.processing.order.database.PostgresTestContainerConfig;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // Triggers Testcontainers via PostgresTestContainerConfig
//@Import(PostgresTestContainerConfig.class) // This works thanks to the test-jar (see pom.xml)!
class OrderServiceApplicationTests {

  @Test
  void contextLoads() {
  }

}
