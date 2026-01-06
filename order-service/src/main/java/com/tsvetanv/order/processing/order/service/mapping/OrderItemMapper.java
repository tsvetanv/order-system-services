package com.tsvetanv.order.processing.order.service.mapping;

import com.tsvetanv.order.processing.order.api.generated.model.Money;
import com.tsvetanv.order.processing.order.api.generated.model.OrderItem;
import com.tsvetanv.order.processing.order.database.entity.OrderItemEntity;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

  @Mapping(target = "productId", source = "productId")
  @Mapping(target = "quantity", source = "quantity")
  @Mapping(target = "unitPrice", expression = "java(toMoney(entity.getUnitAmount(), entity.getCurrency()))")
  @Mapping(target = "lineTotal", expression = "java(toMoney(calculateLineTotal(entity), entity.getCurrency()))")
  OrderItem toApi(OrderItemEntity entity);

  default Money toMoney(BigDecimal amount, String currency) {
    return new Money()
      .amount(amount.toPlainString())
      .currency(currency);
  }

  default BigDecimal calculateLineTotal(OrderItemEntity entity) {
    return entity.getUnitAmount()
      .multiply(BigDecimal.valueOf(entity.getQuantity()));
  }
}
