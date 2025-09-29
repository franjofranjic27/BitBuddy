package ch.ost.clde.ods.kafka;

import ch.ost.clde.dto.MarketDataDto;
import ch.ost.clde.ods.service.OrderDecisionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarketDataConsumer {

    private final OrderDecisionService orderDecisionService;

    @KafkaListener(topics = "market-data-topic", groupId = "order-decision-service-group")
    public void consume(MarketDataDto marketDataDto) {
        orderDecisionService.processTicker(marketDataDto);
    }
}

