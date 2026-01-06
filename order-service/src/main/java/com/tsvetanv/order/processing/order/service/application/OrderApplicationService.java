package com.tsvetanv.order.processing.order.service.application;

import com.tsvetanv.order.processing.order.database.entity.OrderEntity;
import com.tsvetanv.order.processing.order.database.repository.OrderRepository;
import com.tsvetanv.order.processing.order.service.exception.OrderNotFoundException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class OrderApplicationService {

  @Autowired
  private OrderRepository orderRepository;

  @Transactional(readOnly = true)
  public OrderEntity getOrderById(UUID orderId) {
    log.debug("Fetching order with id={}", orderId);

    return orderRepository.findById(orderId)
      .orElseThrow(() -> {
        log.warn("Order with id={} not found", orderId);
        return new OrderNotFoundException(orderId);
      });
  }

  @Transactional
  public UUID createOrder(UUID customerId) {
    log.info("Creating order for customerId={}", customerId);

    OrderEntity entity = new OrderEntity();
    entity.setCustomerId(customerId);
    entity.setStatus("CREATED");

    orderRepository.save(entity);

    log.info("Order created with id={}", entity.getId());
    return entity.getId();
  }
}
