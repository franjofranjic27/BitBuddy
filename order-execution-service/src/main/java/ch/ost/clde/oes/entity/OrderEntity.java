package ch.ost.clde.oes.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
public class OrderEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String customerId;
    private String symbol;
    private Integer quantity;
    private BigDecimal price;
    private String type;
}

