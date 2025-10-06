package ch.ost.clde.oes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.knowm.xchange.dto.trade.MarketOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "market_orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketOrderEntity {
    @Id
    @GeneratedValue
    private UUID id;

    private String base;
    private String counter;
    private String orderType;
    private String orderStyle;

    private BigDecimal amount;

    private final LocalDateTime createdAt = LocalDateTime.now(); // set once, not changeable

    public static MarketOrderEntity from(MarketOrder order) {
        return MarketOrderEntity.builder()
                .base(order.getInstrument().getBase().getCurrencyCode())
                .counter(order.getInstrument().getCounter().getCurrencyCode())
                .orderType(order.getType().toString())
                .orderStyle("MARKET")
                .amount(order.getOriginalAmount())
                .build();
    }
}

