package ch.ost.clde.mds.service;

import ch.ost.clde.dto.MarketDataDto;
import ch.ost.clde.mds.entity.MarketDataEntity;
import ch.ost.clde.mds.kafka.MarketDataProducer;
import ch.ost.clde.mds.repository.MarketDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketDataService {

    private final MarketDataClient marketDataClient;
    private final MarketDataRepository marketDataRepository;
    private final MarketDataProducer marketDataProducer;

    public void fetchTicker(String symbol) {
        MarketDataDto marketDataDto = marketDataClient.getTicker(symbol);
        marketDataRepository.save(new MarketDataEntity(marketDataDto));
        marketDataProducer.send(marketDataDto);
    }
}
