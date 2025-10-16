package ch.ost.clde.mds.stream;

import ch.ost.clde.mds.config.CryptoPairPropteries;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.currency.CurrencyPair;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketDataStream {

    private final CryptoPairPropteries pairPropteries;
    private MarketDataStreamingService streamingService;
    private final MarketDataStreamingServiceFactory factory;

    @PostConstruct
    public void startStreaming() {
        this.streamingService = factory.getStreamingService();
        List<CurrencyPair> pairs = pairPropteries.getPairs().stream()
                .map(s -> {
                    String[] parts = s.split("/");
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Invalid currency pair format: " + s);
                    }
                    return new CurrencyPair(parts[0], parts[1]);
                })
                .toList();

        streamingService.connect(pairs);
        pairs.forEach(streamingService::subscribeTrades);
    }

    @PreDestroy
    public void stopStreaming() {
        streamingService.disconnect();
    }
}

