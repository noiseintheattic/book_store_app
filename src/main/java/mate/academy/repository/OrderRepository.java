package mate.academy.repository;

import java.util.Set;
import mate.academy.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("FROM Order o WHERE o.user.email = :email")
    Set<Order> findByUserEmail(@Param("email")String email);

}
