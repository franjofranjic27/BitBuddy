package ch.ost.clde.oes.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kucoin.api")
public class KucoinProperties {

    private String key;
    private String secret;
    private String passphrase;

    public void setKey(String key) { this.key = key; }

    public void setSecret(String secret) { this.secret = secret; }

    public void setPassphrase(String passphrase) { this.passphrase = passphrase; }
}

