package ch.ost.clde.mds.kafka;

import ch.ost.clde.dto.MarketDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketDataProducer {

    private final KafkaTemplate<String, MarketDataDto> kafkaTemplate;

    public void publishTrade(MarketDataDto marketDataDto) {
        String symbol = marketDataDto.getBase() + "/" + marketDataDto.getCounter();

        kafkaTemplate.send("market-data-topic", symbol, marketDataDto)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish trade to Kafka for {}: {}",
                                symbol, ex.getMessage(), ex);
                    } else {
                        log.debug("Published trade to Kafka: {}", marketDataDto);
                    }
                });
    }

}

