package ch.ost.clde.oes.service;

import ch.ost.clde.dto.OrderDto;
import ch.ost.clde.oes.entity.OrderEntity;
import ch.ost.clde.oes.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class OrderService {

    private final OrderRepository repository;

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
}

