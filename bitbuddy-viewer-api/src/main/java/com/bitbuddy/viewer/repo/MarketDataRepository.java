package com.bitbuddy.viewer.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MarketDataRepository extends Repository<Object, Long> { // <-- HIER
    interface QuoteDtoProjection {
        Long getTs();
        Double getPrice();
        String getSymbol();
    }

    @Query(value = """
      SELECT
        EXTRACT(EPOCH FROM md.timestamp)*1000 AS ts,
        md.price AS price,
        md.base || '/' || md.counter AS symbol
      FROM market_data.market_data md
      WHERE (md.base || '/' || md.counter) = :symbol
      ORDER BY md.timestamp DESC
      LIMIT :limit
      """, nativeQuery = true)
    List<QuoteDtoProjection> findRecentQuotes(@Param("symbol") String symbol, @Param("limit") int limit);
}
