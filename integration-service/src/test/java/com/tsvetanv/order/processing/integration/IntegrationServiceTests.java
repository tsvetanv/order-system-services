package com.tsvetanv.order.processing.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest(classes = TestConfig.class)
@ActiveProfiles("test") // Explicitly define the profile here
class IntegrationServiceTests {

  @Test
  void contextLoads() {
    // Verifies that beans in this module are correctly defined and scan-able
  }
}
