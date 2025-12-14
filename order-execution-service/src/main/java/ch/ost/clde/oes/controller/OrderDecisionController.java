package ch.ost.clde.oes.controller;

import ch.ost.clde.oes.entity.MarketOrderEntity;
import ch.ost.clde.oes.repository.MarketOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/order-exeuction")
@RequiredArgsConstructor
public class OrderDecisionController {

    private final MarketOrderRepository marketOrderRepository;

    @GetMapping
    public List<MarketOrderEntity> getAllMarketData() {
        return marketOrderRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarketOrderEntity> getMarketDataById(@PathVariable UUID id) {
        Optional<MarketOrderEntity> marketData = marketOrderRepository.findById(id);
        return marketData.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

