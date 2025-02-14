package mate.academy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity
@Data
@EqualsAndHashCode(exclude = {"shoppingCart"})
@ToString
@Accessors(chain = true)
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @ManyToOne
    @ToString.Exclude
    private ShoppingCart shoppingCart;
    @NotNull
    @ManyToOne
    @ToString.Exclude
    private Book book;
    @NotNull
    @Min(0)
    private Integer quantity;
}
