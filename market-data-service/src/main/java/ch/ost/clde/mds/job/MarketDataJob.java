package ch.ost.clde.mds.job;

import ch.ost.clde.mds.service.MarketDataService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketDataJob {

    private final MarketDataService marketDataService;

    @Scheduled(fixedRateString = "${mds.job.fixedRate:1000}")
    public void fetchMarketData() {
        marketDataService.fetchTicker("BTC-USD");
    }
}
