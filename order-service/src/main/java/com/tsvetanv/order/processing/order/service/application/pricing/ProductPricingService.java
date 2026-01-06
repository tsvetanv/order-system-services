package com.tsvetanv.order.processing.order.service.application.pricing;

import com.tsvetanv.order.processing.order.service.application.money.Money;
import java.util.UUID;

/**
 * Provides unit pricing for products.
 *
 * <p>This is an abstraction over the pricing source
 * (catalog, ERP, pricing engine, etc.).</p>
 */
public interface ProductPricingService {

  Money getUnitPrice(UUID productId, String currency);
}
