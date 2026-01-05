package com.tsvetanv.order.processing.order.database.repository;

import com.tsvetanv.order.processing.order.database.entity.OrderEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

}
