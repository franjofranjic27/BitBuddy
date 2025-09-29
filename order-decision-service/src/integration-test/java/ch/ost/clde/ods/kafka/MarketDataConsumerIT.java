package ch.ost.clde.ods.kafka;

import ch.ost.clde.dto.MarketDataDto;
import ch.ost.clde.ods.BaseIntegrationTest;
import ch.ost.clde.ods.service.OrderDecisionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.time.Instant;

@EmbeddedKafka(partitions = 1, topics = { "market-data-topic" })
class MarketDataConsumerIT extends BaseIntegrationTest {

    @Autowired
    private KafkaTemplate<String, MarketDataDto> kafkaTemplate;

    @Autowired
    private OrderDecisionService orderDecisionService; // echtes Bean, aber durch TestConfig überschrieben

    @TestConfiguration
    static class TestConfig {
        @Bean
        public OrderDecisionService orderDecisionService() {
            return Mockito.mock(OrderDecisionService.class);
        }
    }

    @Test
    void shouldConsumeMessageFromKafka() throws Exception {
        MarketDataDto dto = new MarketDataDto("ETH/USDT", 2000.0, 0.5, "SELL", "t2", Instant.now());

        kafkaTemplate.send("market-data-topic", dto.getSymbol(), dto);

        // warten, bis Consumer die Nachricht verarbeitet
        Thread.sleep(2000);

        // then
        Awaitility.await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() ->
                        Mockito.verify(orderDecisionService).processTicker(dto)
                );
    }
}
