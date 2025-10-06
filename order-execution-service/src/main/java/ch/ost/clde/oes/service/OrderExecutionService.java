package ch.ost.clde.oes.service;

import ch.ost.clde.dto.MarketOrderDto;
import ch.ost.clde.dto.OrderType;
import ch.ost.clde.oes.config.TradingProperties;
import ch.ost.clde.oes.entity.MarketOrderEntity;
import ch.ost.clde.oes.repository.MarketOrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class OrderExecutionService {

    private final MarketOrderRepository repository;
    private final ExchangeService exchangeService;
    private final TradingProperties tradingProperties;

    public void executeOrder(MarketOrderDto marketOrderDto) {

        MarketOrder marketOrder = mapToMarketOrder(marketOrderDto);

        if (tradingProperties.isExchangeHot()) {
            exchangeService.placeMarketOrder(marketOrder);
        } else {
            log.info("EXCHANGE_HOT=false → Skipping real execution for {}", marketOrder);
        }

        repository.save(MarketOrderEntity.from(marketOrder));
    }

    public MarketOrder mapToMarketOrder(MarketOrderDto dto) {
        CurrencyPair pair = new CurrencyPair(dto.getBase(), dto.getCounter());
        Order.OrderType orderType = dto.getOrderType() == OrderType.BID
                ? Order.OrderType.BID
                : Order.OrderType.ASK;

        return new MarketOrder(orderType, dto.getAmount(), pair);
    }
}

