package com.tsvetanv.order.processing.integration.accounting;

/**
 * Reports finalized orders to an external accounting system.
 *
 * <p>
 * This is a write-only outbound integration boundary. The Order Service does not depend on
 * accounting responses.
 * </p>
 *
 * <p><strong>Current iteration scope:</strong></p>
 * <ul>
 *   <li>Synchronous invocation</li>
 *   <li>Best-effort delivery</li>
 *   <li>No retries or reconciliation</li>
 * </ul>
 *
 * <p>
 * TODO (Future):
 * <ul>
 *   <li>Asynchronous reporting</li>
 *   <li>Idempotency guarantees</li>
 *   <li>Batch and reconciliation flows</li>
 * </ul>
 * </p>
 */
public interface AccountingService {

  void report(AccountingRecord record);

}
