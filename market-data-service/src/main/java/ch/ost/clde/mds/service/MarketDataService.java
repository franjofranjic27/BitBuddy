package ch.ost.clde.mds.service;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Trade;

public interface MarketDataService {
    void processTrade(CurrencyPair pair, Trade trade); // Streaming Trades
}

