package ch.ost.clde.oes.controller;

import ch.ost.clde.dto.OrderDto;
import ch.ost.clde.oes.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/orders")
@Tag(name = "Order Execution API", description = "Erstellen und Verwalten von Orders")
public class OrderController {

    private final OrderService service;

    @PostMapping
    @Operation(summary = "Neue Order erstellen", description = "Nimmt ein OrderDto entgegen und persistiert es")
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto dto) {
        OrderDto savedOrder = service.createOrder(dto);
        return ResponseEntity.ok(savedOrder);
    }
}
