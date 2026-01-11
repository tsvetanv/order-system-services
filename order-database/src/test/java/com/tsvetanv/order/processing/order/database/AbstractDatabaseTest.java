package com.tsvetanv.order.processing.order.database;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(classes = TestApplication.class)
@Import(PostgresTestContainerConfig.class)
public abstract class AbstractDatabaseTest {
  // Keep this class empty or for common test utility methods
}

