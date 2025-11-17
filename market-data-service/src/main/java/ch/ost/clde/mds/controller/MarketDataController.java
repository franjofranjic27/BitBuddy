package ch.ost.clde.mds.controller;

import ch.ost.clde.mds.entity.MarketDataEntity;
import ch.ost.clde.mds.repository.MarketDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/market-data")
@RequiredArgsConstructor
public class MarketDataController {

    private final MarketDataRepository marketDataRepository;

    @GetMapping
    public List<MarketDataEntity> getAllMarketData() {
        return marketDataRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarketDataEntity> getMarketDataById(@PathVariable Long id) {
        Optional<MarketDataEntity> marketData = marketDataRepository.findById(id);
        return marketData.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/symbol/{symbol}")
    public List<MarketDataEntity> getMarketDataBySymbol(@PathVariable String symbol) {
        return marketDataRepository.findBySymbol(symbol);
    }

    @GetMapping("/symbol")
    public List<MarketDataEntity> getMarketDataBySymbolParam(@RequestParam String symbol) {
        return marketDataRepository.findBySymbol(symbol);
    }

    @GetMapping("/latest")
    public List<MarketDataEntity> getLatestMarketData(@RequestParam(defaultValue = "10") int limit) {
        return marketDataRepository.findTopNByOrderByTimestampDesc(limit);
    }

    @GetMapping("/type/{type}")
    public List<MarketDataEntity> getMarketDataByType(@PathVariable String type) {
        return marketDataRepository.findByType(type);
    }
}

