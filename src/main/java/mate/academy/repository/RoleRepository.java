package mate.academy.repository;

import java.util.Set;
import mate.academy.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    @Query("SELECT r FROM Role r "
            + "JOIN FETCH r.usersWithRoles ur "
            + "WHERE ur.email = :email "
            + "AND ur.isDeleted = false")
    Set<Role> findByUsersEmailAndUsersIsDeletedFalse(@Param("email") String email);
}
