package com.tsvetanv.order.processing.order.service.mapping;

import com.tsvetanv.order.processing.order.api.generated.model.CreateOrderRequest;
import com.tsvetanv.order.processing.order.service.application.dto.CreateOrderDto;
import com.tsvetanv.order.processing.order.service.application.dto.CreateOrderItemDto;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreateOrderMapper {

  default CreateOrderDto toDto(CreateOrderRequest request) {
    return new CreateOrderDto(
      request.getCustomer().getCustomerId(),
      request.getItems().stream()
        .map(i -> new CreateOrderItemDto(
          i.getProductId(),
          i.getQuantity()))
        .collect(Collectors.toList()),
      request.getCurrency()
    );
  }
}
