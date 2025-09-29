package ch.ost.clde.mds.service;

import ch.ost.clde.dto.MarketDataDto;
import ch.ost.clde.mds.entity.MarketDataEntity;
import ch.ost.clde.mds.kafka.MarketDataProducer;
import ch.ost.clde.mds.repository.MarketDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Trade;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketDataServiceImpl implements MarketDataService {

    private final MarketDataRepository marketDataRepository;
    private final MarketDataProducer marketDataProducer;


    @Override
    public void processTrade(CurrencyPair pair, Trade trade) {
        log.info("Streaming trade received for {}: {}", pair, trade);

        MarketDataDto dto = new MarketDataDto(
                pair.toString(),
                trade.getPrice().doubleValue(),
                trade.getOriginalAmount().doubleValue(),
                trade.getType() != null ? trade.getType().toString() : null,
                trade.getId(),
                trade.getTimestamp() != null ? trade.getTimestamp().toInstant() : Instant.now()
        );

        MarketDataEntity entity = new MarketDataEntity(dto);

        marketDataRepository.save(entity);
        marketDataProducer.publishTrade(dto);
    }
}
