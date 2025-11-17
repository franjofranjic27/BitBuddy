package ch.ost.clde.ods.controller;

import ch.ost.clde.ods.entity.orderdecision.OrderDecisionEntity;
import ch.ost.clde.ods.repository.orderdecision.OrderDecisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderDecisionController {

    private final OrderDecisionRepository orderDecisionRepository;

    @GetMapping
    public List<OrderDecisionEntity> getAllMarketData() {
        return orderDecisionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDecisionEntity> getMarketDataById(@PathVariable Long id) {
        Optional<OrderDecisionEntity> marketData = orderDecisionRepository.findById(id);
        return marketData.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

//    @GetMapping("/latest")
//    public List<OrderDecisionEntity> getLatestMarketData(@RequestParam(defaultValue = "10") int limit) {
//        return orderDecisionRepository.findTopNByOrderByTimestampDesc(limit);
//    }
}

