package ch.ost.clde.oes.config;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.kucoin.KucoinExchange;
import org.knowm.xchange.service.trade.TradeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XChangeConfig {

    @Bean
    public Exchange kucoinExchange(KucoinProperties props) {
        ExchangeSpecification spec = new KucoinExchange().getDefaultExchangeSpecification();
        spec.setApiKey(props.getKey());
        spec.setSecretKey(props.getSecret());
        spec.setUserName(props.getPassphrase());
        return ExchangeFactory.INSTANCE.createExchange(spec);
    }

    @Bean
    public TradeService tradeService(Exchange kucoinExchange) {
        return kucoinExchange.getTradeService();
    }
}
