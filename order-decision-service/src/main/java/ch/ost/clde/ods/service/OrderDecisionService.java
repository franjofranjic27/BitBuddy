package ch.ost.clde.ods.service;

import ch.ost.clde.dto.MarketDataDto;
import ch.ost.clde.dto.MarketOrderDto;
import ch.ost.clde.dto.OrderType;
import ch.ost.clde.ods.config.OrderDecisionProperties;
import ch.ost.clde.ods.domain.CrossState;
import ch.ost.clde.ods.domain.SymbolMovingAverages;
import ch.ost.clde.ods.kafka.MarketOrderProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderDecisionService {

    private final OrderDecisionProperties properties;
    private final MarketOrderProducer marketOrderProducer;
    private final Map<String, SymbolMovingAverages> symbolCalculators = new ConcurrentHashMap<>();

    public void processTicker(MarketDataDto dto) {
        if (!isTradable(dto.getSymbol())) return;

        SymbolMovingAverages ma = getMovingAverages(dto.getSymbol());
        CrossState crossState = detectCross(ma, dto.getPrice());

        crossState.ifChangedFrom(ma.getLastState(), (oldState, newState) -> {
            handleCrossSignal(dto.getSymbol(), newState);
            ma.setLastState(newState);
        });
    }

    private boolean isTradable(String symbol) {
        return properties.getTradingPairs().contains(symbol);
    }

    private SymbolMovingAverages getMovingAverages(String symbol) {
        return symbolCalculators.computeIfAbsent(symbol, s -> new SymbolMovingAverages());
    }

    private CrossState detectCross(SymbolMovingAverages ma, double price) {
        double avg5 = ma.getMa5().add(price);
        double avg7 = ma.getMa7().add(price);

        log.info("Averages for {}: 5={}, 7={}", ma, avg5, avg7);

        if (!ma.getMa5().isFull() || !ma.getMa7().isFull()) return CrossState.NONE;
        if (avg5 > avg7) return CrossState.ABOVE;
        if (avg5 < avg7) return CrossState.BELOW;
        return CrossState.NONE;
    }

    private void handleCrossSignal(String symbol, CrossState newState) {
        if (newState == CrossState.ABOVE) {
            publishOrder(symbol, OrderType.BID); // BUY
            log.info("Golden Cross (BUY) for {}", symbol);
        } else if (newState == CrossState.BELOW) {
            publishOrder(symbol, OrderType.ASK); // SELL
            log.info("Death Cross (SELL) for {}", symbol);
        }
    }

    private void publishOrder(String symbol, OrderType type) {
        marketOrderProducer.publishOrder(buildOrderDto(symbol, type));
    }

    private MarketOrderDto buildOrderDto(String symbol, OrderType type) {
        MarketOrderDto dto = new MarketOrderDto();
        dto.setBase(symbol.substring(0, 3));
        dto.setCounter(symbol.substring(3));
        dto.setOrderType(type);
        dto.setAmount(BigDecimal.valueOf(0.01)); // FIXME: dynamic position sizing later
        return dto;
    }
}

