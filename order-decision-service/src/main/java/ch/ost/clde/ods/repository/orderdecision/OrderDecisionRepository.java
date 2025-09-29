package ch.ost.clde.ods.repository.orderdecision;

import ch.ost.clde.ods.entity.orderdecision.OrderDecisionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDecisionRepository extends JpaRepository<OrderDecisionEntity, Long> {
}
