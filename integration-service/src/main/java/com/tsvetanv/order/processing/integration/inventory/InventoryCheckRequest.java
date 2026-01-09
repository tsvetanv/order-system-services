package com.tsvetanv.order.processing.integration.inventory;

import java.util.List;
import java.util.UUID;

/**
 * Immutable inventory availability request.
 *
 * <p>
 * This DTO is owned by the Integration Service and must NOT reference domain entities.
 * </p>
 */
public record InventoryCheckRequest(
  UUID orderId,
  List<InventoryItem> items
) {

}
