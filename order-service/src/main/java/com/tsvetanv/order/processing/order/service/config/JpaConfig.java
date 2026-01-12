package com.tsvetanv.order.processing.order.service.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.tsvetanv.order.processing.order.database")
@EntityScan(basePackages = "com.tsvetanv.order.processing.order.database")
public class JpaConfig {

}
