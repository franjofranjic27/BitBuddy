package ch.ost.clde.ods.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "ods")
public class OrderDecisionProperties {

    private List<String> tradingPairs;
}

