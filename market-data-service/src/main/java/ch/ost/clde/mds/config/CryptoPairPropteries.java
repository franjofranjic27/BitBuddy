package ch.ost.clde.mds.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "mds.crypto")
public class CryptoPairPropteries {
    private List<String> pairs;
}

