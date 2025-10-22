package com.usabana.market.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableAutoConfiguration
@EntityScan("com.usabana.market.persistence.entity")
@EnableJpaRepositories("com.usabana.market.persistence.crud")
@EnableTransactionManagement
public class TestJpaConfig {
}
