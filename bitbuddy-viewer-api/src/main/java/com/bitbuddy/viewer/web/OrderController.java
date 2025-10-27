package com.bitbuddy.viewer.web;

import com.bitbuddy.viewer.dto.OrderDto;
import com.bitbuddy.viewer.repo.MarketOrderRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {
    private final MarketOrderRepository orderRepository;

    public OrderController(MarketOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping("/orders")
    public List<OrderDto> orders(@RequestParam(required = false) String symbol,
                                 @RequestParam(required = false) Integer limit) {
        return orderRepository.findRecent(symbol, limit)
                .stream()
                .map(p -> new OrderDto(
                        p.getId(), p.getTs(), p.getSide(), p.getSymbol(), p.getQty(), p.getPrice(), p.getStatus()
                )).toList();
    }
}
