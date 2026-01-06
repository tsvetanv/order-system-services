package com.tsvetanv.order.processing.order.database.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {

  @Id
  private UUID id;

  @Column(name = "customer_id", nullable = false)
  private UUID customerId;

  @Column(nullable = false)
  private String status;

  @OneToMany(
    mappedBy = "order",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  private List<OrderItemEntity> items = new ArrayList<>();

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;
}
