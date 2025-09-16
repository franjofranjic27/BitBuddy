package ch.ost.clde.mds.service;

import ch.ost.clde.mds.dto.MarketDataDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class MarketDataClient {

    private final WebClient webClient;

    public MarketDataClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://api.example.com").build();
    }

    public MarketDataDto getTicker(String symbol) {
        return new MarketDataDto(symbol, 10.0);
        /*
        return webClient.get()
                .uri("/ticker/{symbol}", symbol)
                .retrieve()
                .bodyToMono(TickerDto.class)
                .block(); // block() = synchron, reicht für dein Job
         */

    }
}

