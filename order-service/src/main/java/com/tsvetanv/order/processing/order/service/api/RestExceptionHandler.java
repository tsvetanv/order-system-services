package com.tsvetanv.order.processing.order.service.api;

import com.tsvetanv.order.processing.order.api.generated.model.Error;
import com.tsvetanv.order.processing.order.service.exception.OrderCancellationNotAllowedException;
import com.tsvetanv.order.processing.order.service.exception.OrderNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(OrderNotFoundException.class)
  public ResponseEntity<Error> handleNotFound(OrderNotFoundException ex) {
    log.warn("Order not found", ex);
    return ResponseEntity
      .status(HttpStatus.NOT_FOUND)
      .body(new Error()
        .code("ORDER_NOT_FOUND")
        .message(ex.getMessage()));
  }

  @ExceptionHandler(OrderCancellationNotAllowedException.class)
  public ResponseEntity<Error> handleCancellationNotAllowed(
    OrderCancellationNotAllowedException ex) {
    log.warn("Order cancellation not allowed", ex);
    return ResponseEntity
      .status(HttpStatus.CONFLICT)
      .body(new Error()
        .code("ORDER_CANCELLATION_NOT_ALLOWED")
        .message(ex.getMessage()));
  }
}
