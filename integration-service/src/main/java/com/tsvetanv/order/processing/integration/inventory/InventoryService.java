package com.tsvetanv.order.processing.integration.inventory;

/**
 * Provides inventory availability checks for products.
 *
 * <p>
 * This interface represents a read-only integration boundary. Inventory mutation (reservation,
 * withdrawal, compensation) is intentionally out of scope for the current iteration.
 * </p>
 *
 * <p>
 * TODO (Future):
 * - Introduce reservation APIs when async processing
 *   and distributed consistency become architectural drivers.
 * </p>
 */
public interface InventoryService {

  InventoryCheckResult checkAvailability(InventoryCheckRequest request)
    throws InventoryUnavailableException;
}
