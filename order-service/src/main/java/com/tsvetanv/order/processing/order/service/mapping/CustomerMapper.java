package com.tsvetanv.order.processing.order.service.mapping;

import com.tsvetanv.order.processing.order.api.generated.model.Customer;
import com.tsvetanv.order.processing.order.database.entity.OrderEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

  default Customer fromOrderEntity(OrderEntity entity) {
    return new Customer()
      .customerId(entity.getCustomerId())
      .email("unknown@example.com"); // placeholder until customer service exists
  }
}
