package ch.ost.clde.mds.stream;

import org.knowm.xchange.currency.CurrencyPair;

import java.util.List;

public interface MarketDataStreamingService {
    void connect(List<CurrencyPair> pairs);

    void subscribeTrades(CurrencyPair pair);

    void disconnect();
}

