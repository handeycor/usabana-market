package com.usabana.market;

import com.usabana.market.config.TestJpaConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = TestJpaConfig.class)
class MarketApplicationTests {

    @Test
    void contextLoads() {
        // Prueba que el contexto de la aplicación se carga correctamente
    }
}