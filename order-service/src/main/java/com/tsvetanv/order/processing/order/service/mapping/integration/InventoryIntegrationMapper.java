package com.tsvetanv.order.processing.order.service.mapping.integration;

import com.tsvetanv.order.processing.integration.inventory.InventoryCheckRequest;
import com.tsvetanv.order.processing.integration.inventory.InventoryItem;
import com.tsvetanv.order.processing.order.database.entity.OrderEntity;
import com.tsvetanv.order.processing.order.database.entity.OrderItemEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Anti-Corruption Layer mapper between Order domain and Inventory integration.
 *
 * <p>
 * Domain objects MUST NOT leak into integration contracts.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface InventoryIntegrationMapper {

  @Mapping(target = "orderId", source = "id")
  @Mapping(target = "items", source = "items")
  InventoryCheckRequest toInventoryCheckRequest(OrderEntity order);

  List<InventoryItem> toInventoryItems(List<OrderItemEntity> items);

  @Mapping(target = "productId", source = "productId")
  @Mapping(target = "quantity", source = "quantity")
  InventoryItem toInventoryItem(OrderItemEntity item);
}
