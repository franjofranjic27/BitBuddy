package ch.ost.clde.mds.kafka;

import ch.ost.clde.mds.dto.MarketDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketDataProducer {

    private final KafkaTemplate<String, MarketDataDto> kafkaTemplate;

    public void send(MarketDataDto marketDataDto) {
        kafkaTemplate.send("market-data-topic", marketDataDto.getSymbol(), marketDataDto);
    }
}

