package ch.ost.clde.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketDataDto {
    private String base;
    private String counter;
    private double price;
    private double amount;
    private String type;
    private String tradeId;
    private Instant timestamp;
}

