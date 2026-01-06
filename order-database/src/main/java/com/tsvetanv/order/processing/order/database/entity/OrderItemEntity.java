package com.tsvetanv.order.processing.order.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemEntity {

  @Id
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private OrderEntity order;

  @Column(name = "product_id", nullable = false)
  private UUID productId;

  @Column(nullable = false)
  private int quantity;

  @Column(
    name = "unit_amount",
    nullable = false,
    precision = 19,
    scale = 4
  )
  private BigDecimal unitAmount;

  @Column(nullable = false, length = 3)
  private String currency;
}
