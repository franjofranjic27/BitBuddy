package com.bitbuddy.viewer.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DecisionRepository extends Repository<Object, Long> { // <-- HIER
    interface DecisionProjection {
        Long getTs();
        String getSignal();
        Double getPrice();
        String getSymbol();
    }

    @Query(value = """
      SELECT
        EXTRACT(EPOCH FROM d.timestamp)*1000 AS ts,
        d.signal AS signal,
        d.price AS price,
        d.base || '/' || d.counter AS symbol
      FROM order_decision.order_decision d
      WHERE (d.base || '/' || d.counter) = :symbol
      ORDER BY d.timestamp DESC
      LIMIT :limit
      """, nativeQuery = true)
    List<DecisionProjection> findRecent(@Param("symbol") String symbol, @Param("limit") int limit);
}
