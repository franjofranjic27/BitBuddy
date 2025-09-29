package ch.ost.clde.ods.repository.marketdata;

import ch.ost.clde.ods.entity.marketdata.MarketDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketDataRepository extends JpaRepository<MarketDataEntity, Long> {
    MarketDataEntity findFirstBySymbolOrderByIdDesc(String symbol);
}
