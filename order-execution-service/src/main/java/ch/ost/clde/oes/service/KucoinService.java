package ch.ost.clde.oes.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.service.trade.TradeService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class KucoinService implements ExchangeService {

    private final TradeService tradeService;

    public void placeMarketOrder(MarketOrder marketOrder) {
        try {
            tradeService.placeMarketOrder(marketOrder);
        } catch (Exception e) {
            log.error("Error placing market order: {}", e.getMessage(), e);
        }
    }
}