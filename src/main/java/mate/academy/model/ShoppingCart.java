package mate.academy.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity
@Data
@ToString
@Accessors(chain = true)
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<CartItem> cartItems;

    public ShoppingCart() {
    }

    public ShoppingCart(User user) {
        this.user = user;
    }
}
