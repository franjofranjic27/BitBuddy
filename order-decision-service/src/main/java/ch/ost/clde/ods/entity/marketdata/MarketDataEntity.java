package ch.ost.clde.ods.entity.marketdata;

import ch.ost.clde.dto.MarketDataDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@Entity
@Table(name = "market_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;     // z. B. "BTC/USDT"
    private double price;      // trade.getPrice().doubleValue()
    private double amount;     // trade.getOriginalAmount().doubleValue()
    private String type;       // BUY oder SELL
    private String tradeId;    // eindeutige ID vom Exchange
    private Instant timestamp; // Zeitpunkt des Trades

    public MarketDataEntity(MarketDataDto dto) {
        this.symbol = dto.getBase() + "/" + dto.getCounter();
        this.price = dto.getPrice();
        this.amount = dto.getAmount();
        this.type = dto.getType();
        this.tradeId = dto.getTradeId();
        this.timestamp = dto.getTimestamp();
    }
}



