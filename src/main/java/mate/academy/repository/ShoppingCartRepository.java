package mate.academy.repository;

import java.util.Optional;
import mate.academy.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @Query("FROM ShoppingCart s LEFT JOIN FETCH s.cartItems ci WHERE s.user.email = :email")
    Optional<ShoppingCart> findByUserEmail(@Param("email")String email);
}
