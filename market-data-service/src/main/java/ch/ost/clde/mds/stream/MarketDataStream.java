package ch.ost.clde.mds.stream;

import ch.ost.clde.mds.config.CryptoPairPropteries;
import ch.ost.clde.mds.service.MarketDataService;
import info.bitrich.xchangestream.binance.BinanceStreamingExchange;
import info.bitrich.xchangestream.binance.BinanceSubscriptionType;
import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.rxjava3.disposables.Disposable;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketDataStream {

    private final CryptoPairPropteries pairPropteries;
    private final MarketDataService marketDataService;

    private BinanceStreamingExchange exchange;
    private final Map<CurrencyPair, Disposable> tradeSubscriptions = new HashMap<>();

    @PostConstruct
    public void startStreaming() {
        try {
            // 1. Exchange-Spezifikation
            ExchangeSpecification spec = StreamingExchangeFactory
                    .INSTANCE
                    .createExchange(BinanceStreamingExchange.class)
                    .getDefaultExchangeSpecification();

            exchange = (BinanceStreamingExchange) StreamingExchangeFactory.INSTANCE.createExchange(spec);

            // 2. CurrencyPairs aus Config laden
            List<CurrencyPair> pairs = pairPropteries.getPairs().stream()
                    .map(s -> {
                        String[] parts = s.split("/");
                        if (parts.length != 2) {
                            throw new IllegalArgumentException("Invalid currency pair format: " + s);
                        }
                        return new CurrencyPair(parts[0], parts[1]);
                    })
                    .toList();


            // 3. Initiales Abo (ProductSubscription braucht mind. ein Pair beim Connect)
            ProductSubscription.ProductSubscriptionBuilder builder = ProductSubscription.create();
            pairs.forEach(builder::addTrades);
            ProductSubscription subscription = builder.build();

            exchange.connect(subscription).blockingAwait();
            exchange.enableLiveSubscription();

            // 4. Subscriptions einrichten
            for (CurrencyPair pair : pairs) {
                Disposable sub = exchange.getStreamingMarketDataService()
                        .getTrades(pair)
                        .doOnDispose(() -> exchange.getStreamingMarketDataService()
                                .unsubscribe(pair, BinanceSubscriptionType.TRADE))
                        .subscribe(
                                trade -> {
                                    marketDataService.processTrade(pair, trade);
                                },
                                error -> log.error("Error trades for {}", pair, error)
                        );
                tradeSubscriptions.put(pair, sub);
                log.info("Subscribed to trades for {}", pair);
            }

            log.info("Binance WebSocket subscriptions established for pairs: {}", pairs);
        } catch (Exception e) {
            log.error("Error starting Binance streaming exchange", e);
        }
    }

    @PreDestroy
    public void stopStreaming() {
        try {
            log.info("Stopping Binance streaming...");

            tradeSubscriptions.forEach((pair, sub) -> {
                if (sub != null && !sub.isDisposed()) {
                    sub.dispose();
                    log.info("Unsubscribed from {}", pair);
                }
            });

            if (exchange != null) {
                exchange.disconnect().blockingAwait();
            }

            log.info("Binance WebSocket disconnected.");
        } catch (Exception e) {
            log.error("Error stopping Binance streaming exchange", e);
        }
    }
}
