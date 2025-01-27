package mate.academy.mapper;

import java.util.HashSet;
import java.util.Set;
import mate.academy.config.MapperConfig;
import mate.academy.dto.cart.CartItemDto;
import mate.academy.dto.cart.ShoppingCartDto;
import mate.academy.exceptions.DataProcessingException;
import mate.academy.model.CartItem;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = CartItemMapper.class)
public interface ShoppingCartMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "user", target = "userId", qualifiedByName = "userToId")
    @Mapping(source = "cartItems", target = "cartItems", qualifiedByName = "cartItemsToDto")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);

    @Named("userToId")
    default Long userToId(User user) {
        return user.getId();
    }

    @Named("cartItemsToDto")
    default Set<CartItemDto> cartItemsToDto(Set<CartItem> items) {
        if (items != null) {
            Set<CartItemDto> cartItemsDto = new HashSet<>();
            for (CartItem c : items) {
                CartItemDto cartItemDto = new CartItemDto();
                if (c.getBook() != null) {
                    cartItemDto.setId(c.getId());
                    cartItemDto.setQuantity(c.getQuantity());
                    cartItemDto.setBookId(c.getBook().getId());
                    cartItemDto.setBookTitle(c.getBook().getTitle());
                    cartItemsDto.add(cartItemDto);
                }
            }
            return cartItemsDto;
        } else {
            throw new DataProcessingException("Can't change cartItems into dto,"
                    + "because object 'cart items' is null.");
        }
    }
}
