package com.tsvetanv.order.processing.order.service.mapping;

import com.tsvetanv.order.processing.order.api.generated.model.Money;
import com.tsvetanv.order.processing.order.api.generated.model.OrderItem;
import com.tsvetanv.order.processing.order.database.entity.OrderItemEntity;
import com.tsvetanv.order.processing.order.service.application.pricing.PricingService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Maps {@link OrderItemEntity} to API {@link OrderItem}.
 *
 * <p>No pricing logic is implemented here.
 * All calculations are delegated to {@link PricingService}.</p>
 */
@Mapper(componentModel = "spring")
public abstract class OrderItemMapper {

  @Autowired
  protected PricingService pricingService;

  @Mapping(target = "productId", source = "productId")
  @Mapping(target = "quantity", source = "quantity")
  @Mapping(
    target = "unitPrice",
    expression = "java(toApiMoney(pricingService.calculateUnitPrice(entity)))"
  )
  @Mapping(
    target = "lineTotal",
    expression = "java(toApiMoney(pricingService.calculateLineTotal(entity)))"
  )
  public abstract OrderItem toApi(OrderItemEntity entity);

  protected Money toApiMoney(
    com.tsvetanv.order.processing.order.service.application.money.Money money) {
    if (money == null) {
      return null;
    }
    return new Money()
      .amount(money.amount().toPlainString())
      .currency(money.currency());
  }
}
