package com.tsvetanv.order.processing.order.service.api;

import com.tsvetanv.order.processing.order.api.generated.model.Error;
import com.tsvetanv.order.processing.order.service.exception.OrderNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(OrderNotFoundException.class)
  public ResponseEntity<Error> handleNotFound(OrderNotFoundException ex) {
    log.warn("Order not found error", ex);

    return ResponseEntity.status(404)
      .body(new Error()
        .code("ORDER_NOT_FOUND")
        .message(ex.getMessage()));
  }
}
