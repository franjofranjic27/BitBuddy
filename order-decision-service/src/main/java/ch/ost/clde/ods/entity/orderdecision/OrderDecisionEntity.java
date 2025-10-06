package ch.ost.clde.ods.entity.orderdecision;

import ch.ost.clde.dto.MarketDataDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "order_decision")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDecisionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;     // z. B. "BTC/USDT"
    private double price;      // trade.getPrice().doubleValue()
    private double amount;     // trade.getOriginalAmount().doubleValue()
    private String type;       // BUY oder SELL
    private String tradeId;    // eindeutige ID vom Exchange
    private Instant timestamp; // Zeitpunkt des Trades

    public OrderDecisionEntity(MarketDataDto dto) {
        this.symbol = dto.getBase() + "/" + dto.getCounter();
        this.price = dto.getPrice();
        this.amount = dto.getAmount();
        this.type = dto.getType();
        this.tradeId = dto.getTradeId();
        this.timestamp = dto.getTimestamp();
    }
}



