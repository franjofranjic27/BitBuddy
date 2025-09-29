package ch.ost.clde.ods.repository;

import ch.ost.clde.ods.BaseIntegrationTest;
import ch.ost.clde.ods.entity.marketdata.MarketDataEntity;
import ch.ost.clde.ods.repository.marketdata.MarketDataRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MarketDataRepositoryIT extends BaseIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("secret");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MarketDataRepository repository;

    @Test
    void shouldFindLatestBySymbol() {
        // Erster Wert
        MarketDataEntity e1 = MarketDataEntity.builder()
                .symbol("ETH/USDT")
                .price(2000.0)
                .amount(0.5)
                .type("BUY")
                .tradeId("t1")
                .timestamp(Instant.now())
                .build();

        repository.save(e1);

        // Zweiter (neuerer) Wert
        MarketDataEntity e2 = MarketDataEntity.builder()
                .symbol("ETH/USDT")
                .price(2100.0)
                .amount(0.3)
                .type("SELL")
                .tradeId("t2")
                .timestamp(Instant.now().plusSeconds(60))
                .build();

        repository.save(e2);

        MarketDataEntity latest = repository.findFirstBySymbolOrderByIdDesc("ETH/USDT");

        assertThat(latest.getPrice()).isEqualTo(2100.0);
    }
}

