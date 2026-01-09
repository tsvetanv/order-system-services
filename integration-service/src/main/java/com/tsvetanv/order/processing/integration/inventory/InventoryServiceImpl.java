package com.tsvetanv.order.processing.integration.inventory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Stub inventory integration.
 *
 * <p>
 * Always reports inventory as available.
 * </p>
 */
@Slf4j
@Service
public class InventoryServiceImpl implements InventoryService {

  @Override
  public InventoryCheckResult checkAvailability(
    InventoryCheckRequest request
  ) {
    log.debug(
      "Checking inventory availability | orderId={} | items={}",
      request.orderId(),
      request.items().size()
    );

    return new InventoryCheckResult(true);
  }
}
