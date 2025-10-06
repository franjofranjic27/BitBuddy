package ch.ost.clde.oes.repository;

import ch.ost.clde.oes.entity.MarketOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface MarketOrderRepository extends JpaRepository<MarketOrderEntity, UUID> {
}
