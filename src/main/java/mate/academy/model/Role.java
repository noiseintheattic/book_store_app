package mate.academy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

@Data
@Entity
@Table(name = "roles")
@ToString
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;
    @NotNull
    @Column(unique = true)
    @Enumerated(EnumType.STRING)
    @ToString.Include
    private RoleName name;

    @Override
    public String getAuthority() {
        return name.name();
    }

    private enum RoleName {
        ROLE_ADMIN,
        ROLE_USER
    }
}
