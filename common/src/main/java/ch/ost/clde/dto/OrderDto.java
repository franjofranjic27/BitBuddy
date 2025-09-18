package ch.ost.clde.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "Order Data Transfer Object")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {

    @Schema(description = "Eindeutige Kunden-ID", example = "12345")
    @NotNull
    private String customerId;

    @Schema(description = "Ticker Symbol", example = "AAPL")
    @NotNull
    private String symbol;

    @Schema(description = "Menge", example = "10")
    @NotNull
    private Integer quantity;

    @Schema(description = "Order Preis", example = "185.50")
    private BigDecimal price;

    @Schema(description = "Order Typ", example = "BUY")
    @NotNull
    private String type; // BUY oder SELL
}

