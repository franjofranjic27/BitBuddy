package com.bitbuddy.viewer.web;

import com.bitbuddy.viewer.dto.QuoteDto;
import com.bitbuddy.viewer.repo.MarketDataRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MarketDataController {
    private final MarketDataRepository marketDataRepository;

    public MarketDataController(MarketDataRepository marketDataRepository) {
        this.marketDataRepository = marketDataRepository;
    }

    @GetMapping("/quotes")
    public List<QuoteDto> quotes(@RequestParam String symbol,
                                 @RequestParam(defaultValue = "200") int limit) {
        return marketDataRepository.findRecentQuotes(symbol, Math.min(limit, 2000))
                .stream()
                .map(p -> new QuoteDto(p.getTs(), p.getPrice(), p.getSymbol()))
                .toList();
    }
}
