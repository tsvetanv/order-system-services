package com.tsvetanv.order.processing.integration.notification;

/**
 * Sends notifications related to order lifecycle changes.
 *
 * <p>
 * This interface represents an outbound integration boundary. The Order Service expresses intent by
 * invoking this interface, without knowing how notifications are delivered.
 * </p>
 *
 * <p><strong>Current iteration scope:</strong></p>
 * <ul>
 *   <li>Synchronous execution</li>
 *   <li>Fire-and-forget semantics</li>
 *   <li>No retries, no delivery guarantees</li>
 * </ul>
 *
 * <p>
 * TODO (Future):
 * <ul>
 *   <li>Introduce async delivery (events / messaging)</li>
 *   <li>Add retry and failure handling</li>
 *   <li>Support multiple notification channels</li>
 * </ul>
 * </p>
 */
public interface NotificationService {

  void send(OrderNotification notification);

}
