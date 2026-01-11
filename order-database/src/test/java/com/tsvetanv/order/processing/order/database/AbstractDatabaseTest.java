package com.tsvetanv.order.processing.order.database;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Switch this @ActiveProfiles value from "test" to "local" to switch between Testcontainers and
 * your local DB.
 */
@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
@Import(PostgresTestContainerConfig.class)
public abstract class AbstractDatabaseTest {

}
