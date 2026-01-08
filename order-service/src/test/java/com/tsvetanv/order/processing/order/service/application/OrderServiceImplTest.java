package com.tsvetanv.order.processing.order.service.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tsvetanv.order.processing.integration.payment.PaymentRequest;
import com.tsvetanv.order.processing.integration.payment.PaymentResult;
import com.tsvetanv.order.processing.integration.payment.PaymentService;
import com.tsvetanv.order.processing.integration.payment.PaymentStatus;
import com.tsvetanv.order.processing.order.database.domain.OrderStatus;
import com.tsvetanv.order.processing.order.database.entity.OrderEntity;
import com.tsvetanv.order.processing.order.database.repository.OrderRepository;
import com.tsvetanv.order.processing.order.service.application.dto.CreateOrderDto;
import com.tsvetanv.order.processing.order.service.application.dto.CreateOrderItemDto;
import com.tsvetanv.order.processing.order.service.application.money.Money;
import com.tsvetanv.order.processing.order.service.application.pricing.PricingService;
import com.tsvetanv.order.processing.order.service.application.pricing.ProductPricingService;
import com.tsvetanv.order.processing.order.service.exception.OrderCancellationNotAllowedException;
import com.tsvetanv.order.processing.order.service.exception.OrderNotFoundException;
import com.tsvetanv.order.processing.order.service.exception.PaymentFailedException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

class OrderServiceImplTest {

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private ProductPricingService productPricingService;

  @Mock
  private PaymentService paymentService;

  // IMPORTANT: PricingService is REAL, not mocked
  private PricingService pricingService;

  @InjectMocks
  private OrderServiceImpl orderService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    pricingService = new PricingService();
    orderService.setPricingService(pricingService);
  }

  // ---------------------------------------------------------------------------
  // Helpers
  // ---------------------------------------------------------------------------

  private OrderEntity order(
    OrderStatus status,
    UUID customerId,
    Instant createdAt
  ) {
    return OrderEntity.builder()
      .id(UUID.randomUUID())
      .status(status)
      .customerId(customerId)
      .createdAt(createdAt)
      .build();
  }

  // ---------------------------------------------------------------------------
  // createOrder + PAYMENT SERVICE
  // ---------------------------------------------------------------------------

  @Test
  void createOrder_paymentAuthorized_createsOrderAndCallsPaymentService() {

    UUID productId = UUID.randomUUID();
    UUID customerId = UUID.randomUUID();

    CreateOrderDto dto = new CreateOrderDto(
      customerId,
      List.of(new CreateOrderItemDto(productId, 2)),
      "EUR"
    );

    when(productPricingService.getUnitPrice(any(), any()))
      .thenReturn(new Money(new BigDecimal("10.00"), "EUR"));

    when(paymentService.authorizePayment(any()))
      .thenReturn(new PaymentResult(PaymentStatus.AUTHORIZED, "PAY-123"));

    when(orderRepository.save(any(OrderEntity.class)))
      .thenAnswer(invocation -> invocation.getArgument(0));

    UUID orderId = orderService.createOrder(dto);

    assertThat(orderId).isNotNull();

    ArgumentCaptor<PaymentRequest> captor =
      ArgumentCaptor.forClass(PaymentRequest.class);

    verify(paymentService).authorizePayment(captor.capture());

    PaymentRequest request = captor.getValue();

    assertThat(request.orderId()).isEqualTo(orderId);
    assertThat(request.customerId()).isEqualTo(customerId);
    assertThat(request.currency()).isEqualTo("EUR");
    assertThat(new BigDecimal(request.amount())).isEqualByComparingTo("20.00");

    verify(orderRepository, times(2)).save(any(OrderEntity.class));

  }

  @Test
  void createOrder_paymentDeclined_throwsException_andDoesNotPersist() {

    CreateOrderDto dto = new CreateOrderDto(
      UUID.randomUUID(),
      List.of(new CreateOrderItemDto(UUID.randomUUID(), 1)),
      "EUR"
    );

    when(productPricingService.getUnitPrice(any(), any()))
      .thenReturn(new Money(new BigDecimal("10.00"), "EUR"));

    when(paymentService.authorizePayment(any()))
      .thenReturn(new PaymentResult(PaymentStatus.DECLINED, "DECLINED"));

    assertThatThrownBy(() -> orderService.createOrder(dto))
      .isInstanceOf(PaymentFailedException.class);

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
      .status(OrderStatus.CREATED)
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
      .status(OrderStatus.CREATED)
      .createdAt(Instant.now())
      .build();

    when(orderRepository.findById(orderId))
      .thenReturn(Optional.of(entity));

    orderService.cancelOrder(orderId);

    assertThat(entity.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    verify(orderRepository).save(entity);
  }

  @Test
  void cancelOrder_alreadyCancelled_isIdempotent() {

    UUID orderId = UUID.randomUUID();
    OrderEntity entity = OrderEntity.builder()
      .id(orderId)
      .status(OrderStatus.CANCELLED)
      .createdAt(Instant.now())
      .build();

    when(orderRepository.findById(orderId))
      .thenReturn(Optional.of(entity));

    orderService.cancelOrder(orderId);

    // No state change, no persistence
    verify(orderRepository, never()).save(any());
  }


  @Test
  void cancelOrder_forbiddenStatus_throwsException() {

    UUID orderId = UUID.randomUUID();
    OrderEntity entity = OrderEntity.builder()
      .id(orderId)
      .status(OrderStatus.SHIPPED)
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

    OrderEntity entity = order(
      OrderStatus.CREATED,
      UUID.randomUUID(),
      Instant.now()
    );

    when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
      .thenReturn(new PageImpl<>(List.of(entity)));

    Page<OrderEntity> result =
      orderService.listOrders(10, 0, null, null, null);

    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0)).isSameAs(entity);
  }
}
