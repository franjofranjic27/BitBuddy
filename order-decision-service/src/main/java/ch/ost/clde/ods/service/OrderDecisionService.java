package ch.ost.clde.ods.service;

import ch.ost.clde.dto.MarketDataDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderDecisionService {

    public void processTicker(MarketDataDto marketDataDto) {
        log.info("Processing market data in DecisionService: {} -> {}", marketDataDto.getSymbol(), marketDataDto.getPrice());
    }
}

