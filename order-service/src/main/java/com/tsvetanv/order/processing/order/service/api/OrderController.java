package com.tsvetanv.order.processing.order.service.api;

import com.tsvetanv.order.processing.order.api.generated.OrdersApi;
import com.tsvetanv.order.processing.order.api.generated.model.CreateOrderRequest;
import com.tsvetanv.order.processing.order.api.generated.model.Money;
import com.tsvetanv.order.processing.order.api.generated.model.Order;
import com.tsvetanv.order.processing.order.api.generated.model.PagedOrdersResponse;
import com.tsvetanv.order.processing.order.service.application.OrderApplicationService;
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
  private OrderApplicationService orderService;

  @Autowired
  private OrderMapper orderMapper;

  @Override
  public ResponseEntity<Void> cancelOrder(UUID orderId) {
    return null; // TODO fix later
  }

  @Override
  public ResponseEntity<Order> createOrder(CreateOrderRequest createOrderRequest) {
    return null; // TODO fix later
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
