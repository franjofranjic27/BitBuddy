package ch.ost.clde.oes.service;

import ch.ost.clde.oes.config.KucoinProperties;
import lombok.AllArgsConstructor;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.service.trade.TradeService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KucoinService {

    private final KucoinProperties properties;

//    private final TradeService tradeService;

    public void connect() {
        System.out.println("API Key: " + properties.getKey());
    }

//    public String placeMarketBuy(String base, String counter, double amount) throws Exception {
//        CurrencyPair pair = new CurrencyPair(base, counter); // e.g. BTC/USDT
//        MarketOrder order = new MarketOrder(Order.OrderType.BID, amount, pair);
//        return tradeService.placeMarketOrder(order).toString();
//    }
}