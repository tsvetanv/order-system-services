package com.tsvetanv.order.processing.order.service.api;

import com.tsvetanv.order.processing.order.api.generated.OrdersApi;
import com.tsvetanv.order.processing.order.api.generated.model.CreateOrderRequest;
import com.tsvetanv.order.processing.order.api.generated.model.Money;
import com.tsvetanv.order.processing.order.api.generated.model.Order;
import com.tsvetanv.order.processing.order.api.generated.model.PagedOrdersResponse;
import com.tsvetanv.order.processing.order.service.application.OrderService;
import com.tsvetanv.order.processing.order.service.application.dto.CreateOrderDto;
import com.tsvetanv.order.processing.order.service.mapping.CreateOrderMapper;
import com.tsvetanv.order.processing.order.service.mapping.OrderMapper;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

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
  public ResponseEntity<Void> cancelOrder(UUID orderId) {
    log.info("HTTP DELETE /orders/{}", orderId);
    orderService.cancelOrder(orderId);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Order> createOrder(CreateOrderRequest request) {
    log.info(
      "HTTP POST /orders | customerId={} | items={}",
      request.getCustomer().getCustomerId(),
      request.getItems().size()
    );
    CreateOrderDto createOrderDto = createOrderMapper.toDto(request);
    UUID orderId = orderService.createOrder(createOrderDto);
    log.debug("Fetching created order | orderId={}", orderId);
    var entity = orderService.getOrderById(orderId);
    Order order = orderMapper.toApi(entity);
    log.info("Order created successfully | orderId={}", orderId);
    return ResponseEntity.status(201).body(order);
  }

  @Override
  public ResponseEntity<Order> getOrderById(UUID orderId) {
    log.info("HTTP GET /orders/{}", orderId);

    var entity = orderService.getOrderById(orderId);
    Order order = orderMapper.toApi(entity);

    //  TODO Temporary total until pricing is implemented
    order.setTotalAmount(
      new Money()
        .amount("0.00")
        .currency("EUR")
    );

    return ResponseEntity.ok(order);
  }

  @Override
  public ResponseEntity<PagedOrdersResponse> listOrders(Integer limit, Integer offset) {
    return null;
  }
}
