package ch.ost.clde.mds.repository;

import ch.ost.clde.mds.entity.MarketDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketDataRepository extends JpaRepository<MarketDataEntity, Long> {
    MarketDataEntity findFirstBySymbolOrderByIdDesc(String symbol);
}
