package ch.ost.clde.mds.entity;

import ch.ost.clde.dto.MarketDataDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "market_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;
    private double price;


    public MarketDataEntity(MarketDataDto dto) {
        this.symbol = dto.getSymbol();
        this.price = dto.getPrice();
    }
}
