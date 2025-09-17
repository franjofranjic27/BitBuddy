package ch.ost.clde.mds.service;

import ch.ost.clde.dto.MarketDataDto;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.bitstamp.BitstampExchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@Slf4j
@Component
public class MarketDataClient {

    private final WebClient webClient;

    public MarketDataClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://api.example.com").build();
    }

    public MarketDataDto getTicker(String symbol) {
        try {
            Exchange bitstamp = ExchangeFactory.INSTANCE.createExchange(BitstampExchange.class);
            org.knowm.xchange.service.marketdata.MarketDataService marketDataService = bitstamp.getMarketDataService();
            Ticker ticker = marketDataService.getTicker(CurrencyPair.BTC_USD);

            log.info("Got ticker: {}", ticker);
            return new MarketDataDto(symbol, 10.0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        /*
        return webClient.get()
                .uri("/ticker/{symbol}", symbol)
                .retrieve()
                .bodyToMono(TickerDto.class)
                .block(); // block() = synchron, reicht für dein Job
         */

    }
}

