package ch.ost.clde.oes.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "trading")
public class TradingProperties {
    private boolean exchangeHot;
}
