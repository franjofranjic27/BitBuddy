package ch.ost.clde.mds.repository;

import ch.ost.clde.mds.entity.MarketDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketDataRepository extends JpaRepository<MarketDataEntity, Long> {
    List<MarketDataEntity> findBySymbol(String symbol);

    List<MarketDataEntity> findByType(String type);

    @Query("SELECT m FROM MarketDataEntity m ORDER BY m.timestamp DESC LIMIT :limit")
    List<MarketDataEntity> findTopNByOrderByTimestampDesc(int limit);
}
