package ch.ost.clde.ods.service;

import ch.ost.clde.dto.MarketDataDto;
import ch.ost.clde.ods.config.OrderDecisionProperties;
import ch.ost.clde.ods.kafka.MarketOrderProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.mock;

class OrderDecisionServiceTest {

    private OrderDecisionService service;

    @BeforeEach
    void setUp() {
        OrderDecisionProperties properties = new OrderDecisionProperties();
        properties.setTradingPairs(List.of("BTC/USDT"));
        MarketOrderProducer mockProducer = mock(MarketOrderProducer.class);
        service = new OrderDecisionService(properties, mockProducer);
    }

    private MarketDataDto dto(double price) {
        return new MarketDataDto("BTC/USDT", price, 1.0, "BUY", "t1", Instant.now());
    }

    @Test
    void shouldIgnoreUnconfiguredSymbol() {
        MarketDataDto ethDto = new MarketDataDto("ETH/USDT", 2000.0, 1.0, "BUY", "t2", Instant.now());
        service.processTicker(ethDto);
        // kein Fehler, keine Exception, einfach ignoriert
    }

    @Test
    void shouldGenerateGoldenCrossSignal() {
        // Erst 7 fallende Preise → MA5 < MA7
        for (double p : List.of(100, 99, 98, 97, 96, 95, 94)) {
            service.processTicker(dto(p));
        }
        // Jetzt 5 steigende Preise → MA5 > MA7
        for (double p : List.of(97, 98, 99, 100, 101)) {
            service.processTicker(dto(p));
        }
        // Erwartung: Golden Cross wurde geloggt (prüfen wir über Logs oder Event später)
    }

    @Test
    void shouldGenerateDeathCrossSignal() {
        // Erst 7 steigende Preise → MA5 > MA7
        for (double p : List.of(100, 101, 102, 103, 104, 105, 106)) {
            service.processTicker(dto(p));
        }
        // Jetzt 5 fallende Preise → MA5 < MA7
        for (double p : List.of(105, 104, 103, 102, 101)) {
            service.processTicker(dto(p));
        }
        // Erwartung: Death Cross wurde geloggt
    }

    @Test
    void shouldNotRepeatSameSignal() {
        // viele steigende Preise, MA5 bleibt > MA7
        for (double p : List.of(100, 101, 102, 103, 104, 105, 106, 107, 108, 109)) {
            service.processTicker(dto(p));
        }
        // Erwartung: nur ein Golden Cross, nicht bei jedem Tick
    }
}
