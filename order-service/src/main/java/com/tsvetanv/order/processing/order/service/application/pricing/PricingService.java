package com.tsvetanv.order.processing.order.service.application.pricing;

import com.tsvetanv.order.processing.order.database.entity.OrderEntity;
import com.tsvetanv.order.processing.order.database.entity.OrderItemEntity;
import com.tsvetanv.order.processing.order.service.application.money.Money;
import org.springframework.stereotype.Service;

/**
 * Stateless domain service responsible for all pricing calculations.
 *
 * <p>This is the ONLY place where monetary calculations are allowed.</p>
 */
@Service
public class PricingService {

  public Money calculateUnitPrice(OrderItemEntity item) {
    return new Money(item.getUnitAmount(), item.getCurrency());
  }

  public Money calculateLineTotal(OrderItemEntity item) {
    return calculateUnitPrice(item)
      .multiply(item.getQuantity());
  }

  public Money calculateOrderTotal(OrderEntity order) {

    Money total = null;

    for (OrderItemEntity item : order.getItems()) {
      Money lineTotal = calculateLineTotal(item);
      total = (total == null) ? lineTotal : total.add(lineTotal);
    }

    return total;
  }
}
