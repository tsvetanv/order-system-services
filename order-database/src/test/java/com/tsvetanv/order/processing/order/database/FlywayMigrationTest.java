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

  @Test
  void ordersTableHasStatusColumn() {
    Integer count = jdbcTemplate.queryForObject(
      """
        SELECT COUNT(*)
        FROM information_schema.columns
        WHERE table_name = 'orders'
          AND column_name = 'status'
        """,
      Integer.class
    );

    assertThat(count).isEqualTo(1);
  }

  @Test
  void ordersTableAllowsUpdate() {
    jdbcTemplate.update(
      """
        INSERT INTO orders (id, customer_id, status, created_at, updated_at)
        VALUES (gen_random_uuid(), gen_random_uuid(), 'CREATED', now(), now())
        """
    );

    Integer updated = jdbcTemplate.update(
      """
        UPDATE orders
        SET status = 'CANCELLED'
        """
    );

    assertThat(updated).isGreaterThan(0);
  }

}

