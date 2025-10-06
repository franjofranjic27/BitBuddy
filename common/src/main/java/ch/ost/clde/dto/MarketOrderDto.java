package ch.ost.clde.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketOrderDto {
    private OrderType orderType;
    private BigDecimal amount;
    private String base;
    private String counter;
}


