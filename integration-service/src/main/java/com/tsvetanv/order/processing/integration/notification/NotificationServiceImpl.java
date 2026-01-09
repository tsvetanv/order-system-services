package com.tsvetanv.order.processing.integration.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Stub implementation of NotificationService.
 *
 * <p>
 * This implementation logs notifications and always succeeds.
 * </p>
 */
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

  @Override
  public void send(OrderNotification notification) {
    log.info(
      "Sending notification | type={} | orderId={} | customerId={}",
      notification.type(),
      notification.orderId(),
      notification.customerId()
    );
  }
}
