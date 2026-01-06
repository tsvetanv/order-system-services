package com.tsvetanv.order.processing.order.service.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tsvetanv.order.processing.order.database.entity.OrderEntity;
import com.tsvetanv.order.processing.order.database.repository.OrderRepository;
import com.tsvetanv.order.processing.order.service.application.dto.CreateOrderDto;
import com.tsvetanv.order.processing.order.service.application.dto.CreateOrderItemDto;
import com.tsvetanv.order.processing.order.service.exception.OrderCancellationNotAllowedException;
import com.tsvetanv.order.processing.order.service.exception.OrderNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

class OrderServiceImplTest {

  @Mock
  private OrderRepository orderRepository;

  @InjectMocks
  private OrderServiceImpl orderService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  // ---------------------------------------------------------------------------
  // createOrder
  // ---------------------------------------------------------------------------

  @Test
  void createOrder_createsOrderWithItems() {

    CreateOrderDto dto = new CreateOrderDto(
      UUID.randomUUID(),
      List.of(new CreateOrderItemDto(UUID.randomUUID(), 2)),
      "EUR"
    );

    when(orderRepository.save(any(OrderEntity.class)))
      .thenAnswer(invocation -> invocation.getArgument(0));

    UUID orderId = orderService.createOrder(dto);

    assertThat(orderId).isNotNull();
    verify(orderRepository).save(any(OrderEntity.class));
  }

  // ---------------------------------------------------------------------------
  // getOrderById
  // ---------------------------------------------------------------------------

  @Test
  void getOrderById_returnsOrderWhenFound() {

    UUID orderId = UUID.randomUUID();
    OrderEntity entity = OrderEntity.builder()
      .id(orderId)
      .status(OrderStatus.CREATED.name())
      .createdAt(Instant.now())
      .build();

    when(orderRepository.findById(orderId))
      .thenReturn(Optional.of(entity));

    OrderEntity result = orderService.getOrderById(orderId);

    assertThat(result).isSameAs(entity);
  }

  @Test
  void getOrderById_throwsExceptionWhenNotFound() {

    UUID orderId = UUID.randomUUID();

    when(orderRepository.findById(orderId))
      .thenReturn(Optional.empty());

    assertThatThrownBy(() -> orderService.getOrderById(orderId))
      .isInstanceOf(OrderNotFoundException.class);
  }

  // ---------------------------------------------------------------------------
  // cancelOrder
  // ---------------------------------------------------------------------------

  @Test
  void cancelOrder_allowedStatus_changesStatusToCancelled() {

    UUID orderId = UUID.randomUUID();
    OrderEntity entity = OrderEntity.builder()
      .id(orderId)
      .status(OrderStatus.CREATED.name())
      .createdAt(Instant.now())
      .build();

    when(orderRepository.findById(orderId))
      .thenReturn(Optional.of(entity));

    orderService.cancelOrder(orderId);

    assertThat(entity.getStatus()).isEqualTo(OrderStatus.CANCELLED.name());
    verify(orderRepository).save(entity);
  }

  @Test
  void cancelOrder_alreadyCancelled_isIdempotent() {

    UUID orderId = UUID.randomUUID();
    OrderEntity entity = OrderEntity.builder()
      .id(orderId)
      .status(OrderStatus.CANCELLED.name())
      .createdAt(Instant.now())
      .build();

    when(orderRepository.findById(orderId))
      .thenReturn(Optional.of(entity));

    orderService.cancelOrder(orderId);

    verify(orderRepository, never()).save(any());
  }

  @Test
  void cancelOrder_forbiddenStatus_throwsException() {

    UUID orderId = UUID.randomUUID();
    OrderEntity entity = OrderEntity.builder()
      .id(orderId)
      .status(OrderStatus.SHIPPED.name())
      .createdAt(Instant.now())
      .build();

    when(orderRepository.findById(orderId))
      .thenReturn(Optional.of(entity));

    assertThatThrownBy(() -> orderService.cancelOrder(orderId))
      .isInstanceOf(OrderCancellationNotAllowedException.class);
  }

  // ---------------------------------------------------------------------------
  // listOrders
  // ---------------------------------------------------------------------------

  @Test
  void listOrders_returnsPagedResult() {
    // Arrange
    OrderEntity entity = OrderEntity.builder()
      .id(UUID.randomUUID())
      .status(OrderStatus.CREATED.name())
      .createdAt(Instant.now())
      .build();

    Page<OrderEntity> page = new PageImpl<>(List.of(entity));
    when(orderRepository.findAll(any(Pageable.class)))
      .thenReturn(page);
    // Act
    Page<OrderEntity> result = orderService.listOrders(10, 0);
    // Assert
    assertThat(result.getContent()).hasSize(1);
    verify(orderRepository).findAll(any(Pageable.class));
  }

}
