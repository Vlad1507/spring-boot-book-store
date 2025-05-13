package com.store.bookstore.mapper;

import com.store.bookstore.config.MapperConfig;
import com.store.bookstore.dto.order.OrderDto;
import com.store.bookstore.dto.order.OrderItemDto;
import com.store.bookstore.dto.order.OrderRequestDto;
import com.store.bookstore.models.Order;
import com.store.bookstore.models.OrderItem;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface OrderMapper {
    Order toModel(OrderRequestDto orderRequestDto);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "orderItemsDtos", source = "orderItems", qualifiedByName = "setOrderItems")
    OrderDto toDto(Order order);

    @Named("setOrderItems")
    default Set<OrderItemDto> setOrderItems(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(item ->
                        new OrderItemDto(item.getId(), item.getBook().getId(), item.getQuantity())
                )
                .collect(Collectors.toSet());
    }

}
