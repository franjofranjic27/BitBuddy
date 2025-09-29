package ch.ost.clde.oes.service;

import ch.ost.clde.dto.OrderDto;
import ch.ost.clde.oes.entity.OrderEntity;
import ch.ost.clde.oes.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@AllArgsConstructor
@Service
public class OrderService {

    private final OrderRepository repository;
    private final KucoinService kucoinService;

    public OrderDto createOrder(OrderDto orderDto) {
        OrderEntity order = new OrderEntity();
        order.setCustomerId(orderDto.getCustomerId());
        order.setSymbol(orderDto.getSymbol());
        order.setQuantity(orderDto.getQuantity());
        order.setPrice(orderDto.getPrice());
        order.setType(orderDto.getType());
        repository.save(order);
        return orderDto;
    }

    public void connect() {
        kucoinService.connect();
    }
}

