package mate.academy.mapper;

import java.util.HashSet;
import java.util.Set;
import mate.academy.config.MapperConfig;
import mate.academy.dto.order.OrderDto;
import mate.academy.dto.order.OrderItemDto;
import mate.academy.model.Order;
import mate.academy.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = {ShoppingCartMapper.class, OrderItemMapper.class})
public interface OrderMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "user", target = "userId", qualifiedByName = "userToId")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "total", target = "total")
    @Mapping(source = "orderDate", target = "orderDate")
    @Mapping(source = "orderItems", target = "orderItems", qualifiedByName = "orderItemsToDto")
    OrderDto toDto(Order order);

    @Named("orderItemsToDto")
    default Set<OrderItemDto> orderItemsToDto(Set<OrderItem> items) {
        if (items != null) {
            Set<OrderItemDto> orderItemsDto = new HashSet<>();
            for (OrderItem o : items) {
                OrderItemDto orderItemDto = new OrderItemDto();
                if (o.getBook() != null) {
                    orderItemDto.setId(o.getId());
                    orderItemDto.setQuantity(o.getQuantity());
                    orderItemDto.setBookId(o.getBook().getId());
                    orderItemsDto.add(orderItemDto);
                }
            }
            return orderItemsDto;
        } else {
            return Set.of();
        }
    }

    @Named("statusToString")
    default String statusToString(Order.Status status) {
        if (status == null) {
            return "";
        }
        return status.toString();
    }

}
