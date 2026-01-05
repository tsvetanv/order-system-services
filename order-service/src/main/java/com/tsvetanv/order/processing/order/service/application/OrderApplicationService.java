package com.tsvetanv.order.processing.order.service.application;

import com.tsvetanv.order.processing.order.database.entity.OrderEntity;
import com.tsvetanv.order.processing.order.database.repository.OrderRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderApplicationService {

  private final OrderRepository orderRepository;

  public OrderApplicationService(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @Transactional
  public UUID createOrder(UUID customerId) {
    OrderEntity order = new OrderEntity();
    order.setId(UUID.randomUUID());
    order.setCustomerId(customerId);
    order.setStatus("CREATED");
    order.setCreatedAt(Instant.now());
    order.setUpdatedAt(Instant.now());

    orderRepository.save(order);
    return order.getId();
  }
}
