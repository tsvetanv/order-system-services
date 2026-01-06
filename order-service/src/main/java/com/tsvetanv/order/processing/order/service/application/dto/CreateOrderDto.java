package com.tsvetanv.order.processing.order.service.application.dto;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

//  HTTP / OpenAPI
//      ↓
//  CreateOrderRequest   (generated, API contract)
//      ↓
//  CreateOrderDto       (application layer DTO)
//      ↓
//  OrderEntity          (persistence model)
@Getter
@AllArgsConstructor
public class CreateOrderDto {

  private UUID customerId;
  private List<CreateOrderItemDto> items;
  private String currency;
}
