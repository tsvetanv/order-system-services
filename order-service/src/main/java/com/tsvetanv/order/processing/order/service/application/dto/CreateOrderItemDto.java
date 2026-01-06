package com.tsvetanv.order.processing.order.service.application.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateOrderItemDto {

  private UUID productId;
  private int quantity;
}
