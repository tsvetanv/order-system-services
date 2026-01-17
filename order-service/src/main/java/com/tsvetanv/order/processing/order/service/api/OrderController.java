package com.tsvetanv.order.processing.order.service.api;

import com.tsvetanv.order.processing.order.api.generated.OrdersApi;
import com.tsvetanv.order.processing.order.api.generated.model.CreateOrderRequest;
import com.tsvetanv.order.processing.order.api.generated.model.Order;
import com.tsvetanv.order.processing.order.api.generated.model.PagedOrdersResponse;
import com.tsvetanv.order.processing.order.database.domain.OrderStatus;
import com.tsvetanv.order.processing.order.service.application.OrderService;
import com.tsvetanv.order.processing.order.service.application.dto.CreateOrderDto;
import com.tsvetanv.order.processing.order.service.mapping.CreateOrderMapper;
import com.tsvetanv.order.processing.order.service.mapping.OrderMapper;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST adapter for Order use cases.
 */
@Slf4j
@RestController
public class OrderController implements OrdersApi {

  @Autowired
  private OrderService orderService;

  @Autowired
  private CreateOrderMapper createOrderMapper;

  @Autowired
  private OrderMapper orderMapper;

  @Override
  public ResponseEntity<Order> createOrder(CreateOrderRequest request) {
    log.info(
      "HTTP POST /orders | customerId={} | items={}",
      request.getCustomer().getCustomerId(),
      request.getItems().size()
    );

    // Map API request → application DTO
    CreateOrderDto dto = createOrderMapper.toDto(request);

    // Delegate business logic
    UUID orderId = orderService.createOrder(dto);

    // Fetch fully populated aggregate
    var entity = orderService.getOrderById(orderId);

    // Map domain → API model (NO hardcoded values)
    Order response = orderMapper.toApi(entity);

    log.info("Order created successfully | orderId={}", orderId);
    return ResponseEntity.status(201).body(response);
  }

  @Override
  public ResponseEntity<Order> getOrderById(UUID orderId) {
    log.info("HTTP GET /orders/{}", orderId);

    var entity = orderService.getOrderById(orderId);

    // IMPORTANT:
    // totalAmount MUST come from domain mapping,
    // NOT from controller hacks
    Order response = orderMapper.toApi(entity);

    return ResponseEntity.ok(response);
  }

  @Override
  public ResponseEntity<Void> cancelOrder(UUID orderId) {
    log.info("HTTP DELETE /orders/{}", orderId);

    orderService.cancelOrder(orderId);

    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<PagedOrdersResponse> listOrders(
    Integer limit,
    Integer offset,
    com.tsvetanv.order.processing.order.api.generated.model.OrderStatus status,
    UUID customerId,
    String sort
  ) {

    log.info(
      "HTTP GET /orders | limit={} | offset={} | status={} | customerId={} | sort={}",
      limit, offset, status, customerId, sort
    );

    var page = orderService.listOrders(
      limit,
      offset,
      status != null ? OrderStatus.valueOf(status.name()) : null,
      customerId,
      sort
    );

    var response = new PagedOrdersResponse()
      .items(page.getContent().stream()
        .map(orderMapper::toApi)
        .toList())
      .totalElements((int) page.getTotalElements())
      .limit(limit)
      .offset(offset);

    return ResponseEntity.ok(response);
  }
}
