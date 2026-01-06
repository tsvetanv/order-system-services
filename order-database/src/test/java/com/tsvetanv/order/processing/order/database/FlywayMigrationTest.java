package com.tsvetanv.order.processing.order.database;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class FlywayMigrationTest extends AbstractDatabaseTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  void ordersTableExists() {
    Integer count = jdbcTemplate.queryForObject(
      "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'orders'",
      Integer.class);
    assertThat(count).isEqualTo(1);
  }

  @Test
  void orderItemsTableExists() {
    Integer count = jdbcTemplate.queryForObject(
      "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'order_items'",
      Integer.class);
    assertThat(count).isEqualTo(1);
  }

  @Test
  void orderItemsHasForeignKey() {
    Integer count = jdbcTemplate.queryForObject(
      """
        SELECT COUNT(*)
        FROM information_schema.table_constraints
        WHERE table_name = 'order_items'
          AND constraint_type = 'FOREIGN KEY'
        """,
      Integer.class);

    assertThat(count).isGreaterThan(0);
  }
}

