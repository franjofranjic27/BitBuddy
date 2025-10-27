package com.bitbuddy.viewer.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MarketOrderRepository extends Repository<Object, String> { // <-- HIER
    interface OrderProjection {
        String getId();
        Long getTs();
        String getSide();
        String getSymbol();
        Double getQty();
        Double getPrice();
        String getStatus();
    }

    @Query(value = """
      SELECT
        CAST(o.id AS text) AS id,
        EXTRACT(EPOCH FROM o.created_at)*1000 AS ts,
        o.side AS side,
        o.base || '/' || o.counter AS symbol,
        o.amount AS qty,
        o.price AS price,
        o.status AS status
      FROM order_execution.market_orders o
      WHERE (:symbol IS NULL OR (o.base || '/' || o.counter) = :symbol)
      ORDER BY o.created_at DESC
      LIMIT COALESCE(:limit, 200)
      """, nativeQuery = true)
    List<OrderProjection> findRecent(@Param("symbol") String symbol, @Param("limit") Integer limit);
}
