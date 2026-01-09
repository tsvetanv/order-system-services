package com.tsvetanv.order.processing.integration.inventory;

public class InventoryUnavailableException extends RuntimeException {

  public InventoryUnavailableException(String message) {
    super(message);
  }
}
