package com.tsvetanv.order.processing.order.database;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Switch this @ActiveProfiles value from "test" to "local" to switch between Testcontainers and
 * your local DB.
 * <p/>
 * Base class for database integration tests. Use @ActiveProfiles("local") to test against your
 * Rancher Desktop instance.
 */
@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test") // Default; overridden by -Dspring.profiles.active in scripts
@Import(PostgresTestContainerConfig.class)
public abstract class AbstractDatabaseTest {

}
