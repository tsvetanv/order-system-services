package com.tsvetanv.order.processing.order.service.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsvetanv.order.processing.order.api.generated.model.Money;
import com.tsvetanv.order.processing.order.api.generated.model.Order;
import com.tsvetanv.order.processing.order.database.domain.OrderStatus;
import com.tsvetanv.order.processing.order.database.entity.OrderEntity;
import com.tsvetanv.order.processing.order.service.application.OrderService;
import com.tsvetanv.order.processing.order.service.application.dto.CreateOrderDto;
import com.tsvetanv.order.processing.order.service.exception.OrderNotFoundException;
import com.tsvetanv.order.processing.order.service.mapping.CreateOrderMapper;
import com.tsvetanv.order.processing.order.service.mapping.OrderMapper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
class OrderControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private OrderService orderService;

  @MockBean
  private CreateOrderMapper createOrderMapper;

  @MockBean
  private OrderMapper orderMapper;

  // ---------------------------------------------------------------------------
  // POST /orders
  // ---------------------------------------------------------------------------

  @Test
  void createOrder_returns201AndOrder() throws Exception {

    UUID orderId = UUID.randomUUID();
    UUID customerId = UUID.randomUUID();

    String requestJson = """
      {
        "customer": {
          "customerId": "%s"
        },
        "items": [
          {
            "productId": "%s",
            "quantity": 2
          }
        ],
        "currency": "EUR"
      }
      """.formatted(customerId, UUID.randomUUID());

    CreateOrderDto dto = new CreateOrderDto(
      customerId,
      List.of(),
      "EUR"
    );

    OrderEntity entity = OrderEntity.builder()
      .id(orderId)
      .customerId(customerId)
      .status(OrderStatus.CREATED)
      .createdAt(Instant.now())
      .build();

    Order apiOrder = new Order()
      .orderId(orderId)
      .status(com.tsvetanv.order.processing.order.api.generated.model.OrderStatus.CREATED)
      .totalAmount(new Money().amount("20.00").currency("EUR"));

    when(createOrderMapper.toDto(any())).thenReturn(dto);
    when(orderService.createOrder(dto)).thenReturn(orderId);
    when(orderService.getOrderById(orderId)).thenReturn(entity);
    when(orderMapper.toApi(entity)).thenReturn(apiOrder);

    mockMvc.perform(post("/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestJson))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.orderId").value(orderId.toString()))
      .andExpect(jsonPath("$.status").value("CREATED"))
      .andExpect(jsonPath("$.totalAmount.amount").value("20.00"))
      .andExpect(jsonPath("$.totalAmount.currency").value("EUR"));
  }

  // ---------------------------------------------------------------------------
  // GET /orders/{id}
  // ---------------------------------------------------------------------------

  @Test
  void getOrderById_returns200() throws Exception {

    UUID orderId = UUID.randomUUID();

    OrderEntity entity = OrderEntity.builder()
      .id(orderId)
      .status(OrderStatus.CREATED)
      .createdAt(Instant.now())
      .build();

    Order apiOrder = new Order()
      .orderId(orderId)
      .status(com.tsvetanv.order.processing.order.api.generated.model.OrderStatus.CREATED);

    when(orderService.getOrderById(orderId)).thenReturn(entity);
    when(orderMapper.toApi(entity)).thenReturn(apiOrder);

    mockMvc.perform(get("/orders/{id}", orderId))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.orderId").value(orderId.toString()))
      .andExpect(jsonPath("$.status").value("CREATED"));
  }

  @Test
  void getOrderById_notFound_returns404() throws Exception {

    UUID orderId = UUID.randomUUID();

    when(orderService.getOrderById(orderId))
      .thenThrow(new OrderNotFoundException(orderId));

    mockMvc.perform(get("/orders/{id}", orderId))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.code").value("ORDER_NOT_FOUND"));
  }

  // ---------------------------------------------------------------------------
  // DELETE /orders/{id}
  // ---------------------------------------------------------------------------

  @Test
  void cancelOrder_returns204() throws Exception {

    UUID orderId = UUID.randomUUID();

    doNothing().when(orderService).cancelOrder(orderId);

    mockMvc.perform(delete("/orders/{id}", orderId))
      .andExpect(status().isNoContent());
  }
}
