package ch.ost.clde.oes.kafka;

import ch.ost.clde.dto.MarketOrderDto;
import ch.ost.clde.oes.service.OrderExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarketOrderConsumer {

    private final OrderExecutionService orderExecutionService;

    @KafkaListener(topics = "market-order-topic", groupId = "order-execution-service-group")
    public void consume(MarketOrderDto marketOrderDto) {
        orderExecutionService.executeOrder(marketOrderDto);
    }
}

