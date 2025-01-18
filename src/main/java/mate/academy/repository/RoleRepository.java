package mate.academy.repository;

import java.util.Optional;
import mate.academy.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("SELECT r FROM Role r LEFT JOIN FETCH r.userRoles WHERE r.id = :roleId")
    Optional<Role> findByIdWithUsers(@Param("roleId") Long id);
}
