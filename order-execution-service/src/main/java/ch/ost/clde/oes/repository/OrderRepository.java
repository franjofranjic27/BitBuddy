package ch.ost.clde.oes.repository;

import ch.ost.clde.oes.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
}
