package ch.ost.clde.ods.service;

import ch.ost.clde.dto.MarketDataDto;
import ch.ost.clde.ods.config.OrderDecisionProperties;
import ch.ost.clde.ods.utility.MovingAverageCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderDecisionService {

    private final OrderDecisionProperties properties;

    private final Map<String, SymbolMovingAverages> symbolCalculators = new ConcurrentHashMap<>();

    public void processTicker(MarketDataDto dto) {

        if (!properties.getTradingPairs().contains(dto.getSymbol())) {
            return;
        }

        SymbolMovingAverages ma = symbolCalculators.computeIfAbsent(
                dto.getSymbol(),
                s -> new SymbolMovingAverages()
        );

        double avg5 = ma.ma5.add(dto.getPrice());
        double avg7 = ma.ma7.add(dto.getPrice());

        if (ma.ma5.isFull() && ma.ma7.isFull()) {
            CrossState newState;
            log.info("Averages for {}: 5={}, 7={}", dto.getSymbol(), avg5, avg7);
            if (avg5 > avg7) {
                newState = CrossState.ABOVE;
            } else if (avg5 < avg7) {
                newState = CrossState.BELOW;
            } else {
                newState = CrossState.NONE;
            }

            // Nur wenn sich der Zustand geändert hat → Signal ausgeben
            if (newState != ma.lastState) {
                if (ma.lastState == CrossState.BELOW && newState == CrossState.ABOVE) {
                    log.info("Golden Cross (BUY) for {}", dto.getSymbol());
                } else if (ma.lastState == CrossState.ABOVE && newState == CrossState.BELOW) {
                    log.info("Death Cross (SELL) for {}", dto.getSymbol());
                }
                ma.lastState = newState;
            }
        }
    }

    private static class SymbolMovingAverages {
        private final MovingAverageCalculator ma5 = new MovingAverageCalculator(5);
        private final MovingAverageCalculator ma7 = new MovingAverageCalculator(7);
        private CrossState lastState = CrossState.NONE;
    }

    private enum CrossState {
        ABOVE, BELOW, NONE
    }
}

