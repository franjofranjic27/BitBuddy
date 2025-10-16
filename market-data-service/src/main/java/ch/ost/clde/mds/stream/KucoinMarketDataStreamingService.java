package ch.ost.clde.mds.stream;

import ch.ost.clde.mds.service.MarketDataService;
import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import info.bitrich.xchangestream.kucoin.KucoinStreamingExchange;
import io.reactivex.rxjava3.disposables.Disposable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KucoinMarketDataStreamingService implements MarketDataStreamingService {

    private final MarketDataService marketDataService;
    private KucoinStreamingExchange exchange;
    private final Map<CurrencyPair, Disposable> tradeSubscriptions = new HashMap<>();

    @Override
    public void connect(List<CurrencyPair> pairs) {
        try {
            ExchangeSpecification spec = StreamingExchangeFactory
                    .INSTANCE
                    .createExchange(KucoinStreamingExchange.class)
                    .getDefaultExchangeSpecification();

            exchange = (KucoinStreamingExchange) StreamingExchangeFactory.INSTANCE.createExchange(spec);

            ProductSubscription.ProductSubscriptionBuilder builder = ProductSubscription.create();
            pairs.forEach(builder::addTrades);
            ProductSubscription subscription = builder.build();

            exchange.connect(subscription).blockingAwait();

            log.info("Kucoin WebSocket subscriptions established for pairs: {}", pairs);
        } catch (Exception e) {
            log.error("Error starting Kucoin streaming exchange", e);
            throw new RuntimeException("Failed to connect to Kucoin exchange", e);
        }
    }

    @Override
    public void subscribeTrades(CurrencyPair pair) {
        try {
            if (exchange == null) {
                log.error("Cannot subscribe to trades for {}: Exchange not connected", pair);
                return;
            }

            Disposable sub = exchange.getStreamingMarketDataService()
                    .getTrades(pair)
                    .subscribe(
                            trade -> {
                                log.info("Empfangenes Trade für {}: {}", pair, trade);
                                marketDataService.processTrade(pair, trade);
                            },
                            error -> {
                                if (error instanceof NotYetImplementedForExchangeException) {
                                    log.error("Trade streaming is NOT IMPLEMENTED for KuCoin in xchange-stream library. " +
                                            "Please switch to Binance, Kraken, or Bitstamp, or use KuCoin's native WebSocket API directly.");
                                } else {
                                    log.error("Error receiving trades for {}: {}", pair, error.getMessage(), error);
                                }
                            }
                    );
            tradeSubscriptions.put(pair, sub);
            log.info("Subscribed to trades for {}", pair);
        } catch (NotYetImplementedForExchangeException e) {
            log.error("Trade streaming NOT IMPLEMENTED for {} on KuCoin. " +
                    "Switch to Binance, Kraken, or Bitstamp instead.", pair, e);
        } catch (Exception e) {
            log.error("Failed to subscribe to trades for {}: {}", pair, e.getMessage(), e);
        }
    }

    @Override
    public void disconnect() {
        tradeSubscriptions.forEach((pair, sub) -> {
            if (sub != null && !sub.isDisposed()) {
                sub.dispose();
                log.info("Unsubscribed from {}", pair);
            }
        });

        if (exchange != null) {
            try {
                exchange.disconnect().blockingAwait();
                log.info("Kucoin WebSocket disconnected.");
            } catch (Exception e) {
                log.error("Error disconnecting from Kucoin: {}", e.getMessage(), e);
            }
        }
    }
}