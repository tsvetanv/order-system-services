package com.tsvetanv.order.processing.order.service.application;

import com.tsvetanv.order.processing.order.database.domain.OrderStatus;
import com.tsvetanv.order.processing.order.database.entity.OrderEntity;
import com.tsvetanv.order.processing.order.service.application.dto.CreateOrderDto;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface OrderService {

  UUID createOrder(CreateOrderDto dto);

  OrderEntity getOrderById(UUID orderId);

  void cancelOrder(UUID orderId);

  Page<OrderEntity> listOrders(
    int limit,
    int offset,
    OrderStatus status,
    UUID customerId,
    String sort
  );
}
