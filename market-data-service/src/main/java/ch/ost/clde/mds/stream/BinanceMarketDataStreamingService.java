package ch.ost.clde.mds.stream;

import ch.ost.clde.mds.service.MarketDataService;
import info.bitrich.xchangestream.binance.BinanceStreamingExchange;
import info.bitrich.xchangestream.binance.BinanceSubscriptionType;
import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.rxjava3.disposables.Disposable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BinanceMarketDataStreamingService implements MarketDataStreamingService {

    private final MarketDataService marketDataService;
    private BinanceStreamingExchange exchange;
    private final Map<CurrencyPair, Disposable> tradeSubscriptions = new HashMap<>();

    @Override
    public void connect(List<CurrencyPair> pairs) {
        try {
            ExchangeSpecification spec = StreamingExchangeFactory
                    .INSTANCE
                    .createExchange(BinanceStreamingExchange.class)
                    .getDefaultExchangeSpecification();

            exchange = (BinanceStreamingExchange) StreamingExchangeFactory.INSTANCE.createExchange(spec);

            ProductSubscription.ProductSubscriptionBuilder builder = ProductSubscription.create();
            pairs.forEach(builder::addTrades);
            ProductSubscription subscription = builder.build();

            exchange.connect(subscription).blockingAwait();
            exchange.enableLiveSubscription();

            log.info("Binance WebSocket subscriptions established for pairs: {}", pairs);
        } catch (Exception e) {
            log.error("Error starting Binance streaming exchange", e);
        }
    }

    @Override
    public void subscribeTrades(CurrencyPair pair) {
        Disposable sub = exchange.getStreamingMarketDataService()
                .getTrades(pair)
                .doOnDispose(() -> exchange.getStreamingMarketDataService()
                        .unsubscribe(pair, BinanceSubscriptionType.TRADE))
                .subscribe(
                        trade -> marketDataService.processTrade(pair, trade),
                        error -> log.error("Error trades for {}", pair, error)
                );
        tradeSubscriptions.put(pair, sub);
        log.info("Subscribed to trades for {}", pair);
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
            exchange.disconnect().blockingAwait();
        }

        log.info("Binance WebSocket disconnected.");
    }
}

