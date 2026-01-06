package com.tsvetanv.order.processing.order.service.application;

import com.tsvetanv.order.processing.order.database.domain.OrderStatus;
import com.tsvetanv.order.processing.order.database.entity.OrderEntity;
import com.tsvetanv.order.processing.order.service.application.dto.CreateOrderDto;
import com.tsvetanv.order.processing.order.service.exception.OrderCancellationNotAllowedException;
import com.tsvetanv.order.processing.order.service.exception.OrderNotFoundException;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface OrderService {

  UUID createOrder(CreateOrderDto dto);

  OrderEntity getOrderById(UUID orderId);

  /**
   * Cancels an existing order.
   *
   * <p><strong>Business rules:</strong></p>
   * <ul>
   *   <li>An order can be cancelled only if its status is {@link OrderStatus#CREATED} or {@link OrderStatus#CONFIRMED}.</li>
   *   <li>An order cannot be cancelled if its status is {@link OrderStatus#PAID} or {@link OrderStatus#SHIPPED}.</li>
   *   <li>Calling cancel on an already {@link OrderStatus#CANCELLED} order is idempotent and has no effect.</li>
   * </ul>
   *
   * <p><strong>Behavior:</strong></p>
   * <ul>
   *   <li>If the order does not exist, {@link OrderNotFoundException} is thrown.</li>
   *   <li>If cancellation is not allowed due to the current order status,
   *       {@link OrderCancellationNotAllowedException}
   *       is thrown.</li>
   *   <li>On successful cancellation, the order status is set to {@link OrderStatus#CANCELLED}.</li>
   * </ul>
   *
   * @param orderId the identifier of the order to cancel
   */
  void cancelOrder(UUID orderId);

  /**
   * Returns a paginated list of orders with optional filtering and sorting.
   *
   * <p><strong>Filtering:</strong></p>
   * <ul>
   *   <li>{@code status} – filter by order lifecycle status</li>
   *   <li>{@code customerId} – filter orders for a specific customer</li>
   * </ul>
   *
   * <p><strong>Sorting:</strong></p>
   * <ul>
   *   <li>Format: {@code property,(asc|desc)}</li>
   *   <li>Supported properties: {@code createdAt}, {@code updatedAt}</li>
   *   <li>If missing or invalid, defaults to {@code createdAt DESC}</li>
   * </ul>
   *
   * <p><strong>Pagination:</strong></p>
   * <ul>
   *   <li>{@code limit} – maximum number of results per page</li>
   *   <li>{@code offset} – number of records to skip</li>
   * </ul>
   *
   * @param limit      maximum number of orders to return
   * @param offset     number of orders to skip
   * @param status     optional order status filter
   * @param customerId optional customer identifier filter
   * @param sort       optional sort definition
   * @return a page of matching orders
   */
  Page<OrderEntity> listOrders(
    int limit,
    int offset,
    OrderStatus status,
    UUID customerId,
    String sort
  );
}
