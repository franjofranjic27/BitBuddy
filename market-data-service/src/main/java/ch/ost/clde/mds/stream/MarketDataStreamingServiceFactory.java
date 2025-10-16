package ch.ost.clde.mds.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class MarketDataStreamingServiceFactory {

    @Value("${mds.provider}")
    private String providerBeanName;

    @Autowired
    private ApplicationContext context;

    public MarketDataStreamingService getStreamingService() {
        return (MarketDataStreamingService) context.getBean(providerBeanName);
    }
}

