package com.tsvetanv.order.processing.order.service.mapping;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TimeMapper {

  @Named("toOffset")
  default OffsetDateTime toOffset(java.time.Instant instant) {
    return instant != null
      ? instant.atOffset(ZoneOffset.UTC)
      : null;
  }
}
