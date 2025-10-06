package ch.ost.clde.oes.kafka;

import ch.ost.clde.dto.MarketOrderDto;
import ch.ost.clde.oes.BaseIntegrationTest;
import ch.ost.clde.oes.service.OrderExecutionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.math.BigDecimal;
import java.time.Duration;

@EmbeddedKafka(partitions = 1, topics = { "market-data-topic" })
class MarketOrderConsumerIT extends BaseIntegrationTest {

    @Autowired
    private KafkaTemplate<String, MarketOrderDto> kafkaTemplate;

    @Autowired
    private OrderExecutionService orderExecutionService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public OrderExecutionService orderExecutionService() {
            return Mockito.mock(OrderExecutionService.class);
        }
    }

    @Test
    void shouldConsumeMessageFromKafka() throws Exception {
        MarketOrderDto dto = new MarketOrderDto();
        dto.setSymbol("BTC/USDT");
        dto.setPrice(BigDecimal.valueOf(65000.0));
        dto.setQuantity(5);

        kafkaTemplate.send("market-data-topic", dto.getSymbol(), dto);

        // warten, bis Consumer die Nachricht verarbeitet
        Thread.sleep(2000);

        // then
        Awaitility.await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() ->
                        Mockito.verify(orderExecutionService).createOrder(dto)
                );
    }
}
