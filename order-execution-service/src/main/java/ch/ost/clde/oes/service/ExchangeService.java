package ch.ost.clde.oes.service;

import org.knowm.xchange.dto.trade.MarketOrder;

public interface ExchangeService {
    void placeMarketOrder(MarketOrder order);
}
