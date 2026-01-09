package com.tsvetanv.order.processing.integration.inventory;

import java.util.UUID;

public record InventoryItem(
  UUID productId,
  int quantity
) {

}
