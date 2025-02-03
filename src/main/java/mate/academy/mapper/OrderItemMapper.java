package mate.academy.mapper;

import mate.academy.config.MapperConfig;
import mate.academy.dto.order.OrderItemDto;
import mate.academy.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = CartItemMapper.class)
public interface OrderItemMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "book", target = "bookId", qualifiedByName = "bookToId")
    @Mapping(source = "quantity", target = "quantity")
    OrderItemDto toDto(OrderItem orderItem);

}
