package mate.academy.mapper;

import mate.academy.config.MapperConfig;
import mate.academy.dto.order.OrderDto;
import mate.academy.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = ShoppingCartMapper.class)
public interface OrderMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "user", target = "userId", qualifiedByName = "userToId")
    @Mapping(source = "status", target = "status", qualifiedByName = "statusToString")
    @Mapping(source = "total", target = "total")
    @Mapping(source = "orderItems", target = "orderItems", qualifiedByName = "orderItemsToDto")
    OrderDto toDto(Order order);

}
