package ch.ost.clde.ods.kafka;

import ch.ost.clde.dto.MarketOrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketOrderProducer {

    private final KafkaTemplate<String, MarketOrderDto> kafkaTemplate;

    public void publishOrder(MarketOrderDto marketOrderDto) {
        kafkaTemplate.send("market-order-topic", marketOrderDto.getOrderType().toString(), marketOrderDto)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish trade to Kafka for {}: {}",
                                marketOrderDto.getOrderType(), ex.getMessage(), ex);
                    } else {
                        log.info("Published trade to Kafka: {}", marketOrderDto);
                    }
                });
    }

}

