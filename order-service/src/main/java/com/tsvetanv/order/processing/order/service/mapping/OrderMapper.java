package com.tsvetanv.order.processing.order.service.mapping;

import com.tsvetanv.order.processing.order.api.generated.model.Order;
import com.tsvetanv.order.processing.order.database.entity.OrderEntity;
import com.tsvetanv.order.processing.order.service.application.pricing.PricingService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
  componentModel = "spring",
  uses = {
    CustomerMapper.class,
    OrderItemMapper.class,
    TimeMapper.class
  }
)
public abstract class OrderMapper {

  @Autowired
  protected CustomerMapper customerMapper;

  @Autowired
  protected PricingService pricingService;

  @Mapping(target = "orderId", source = "id")
  @Mapping(target = "status", source = "status")
  @Mapping(
    target = "customer",
    expression = "java(customerMapper.fromOrderEntity(entity))"
  )
  @Mapping(target = "items", source = "items")
  @Mapping(
    target = "totalAmount",
    expression = "java(toApiMoney(pricingService.calculateOrderTotal(entity)))"
  )
  @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "toOffset")
  @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "toOffset")
  public abstract Order toApi(OrderEntity entity);

  protected com.tsvetanv.order.processing.order.api.generated.model.Money toApiMoney(
    com.tsvetanv.order.processing.order.service.application.money.Money money
  ) {
    if (money == null) {
      return null;
    }
    return new com.tsvetanv.order.processing.order.api.generated.model.Money()
      .amount(money.amount().toPlainString())
      .currency(money.currency());
  }
}
