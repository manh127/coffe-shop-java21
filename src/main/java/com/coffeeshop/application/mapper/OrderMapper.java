package com.coffeeshop.application.mapper;

import com.coffeeshop.application.dto.OrderDto;
import com.coffeeshop.application.dto.OrderItemDto;
import com.coffeeshop.domain.order.Order;
import com.coffeeshop.domain.order.OrderItem;
import com.coffeeshop.domain.shared.Money;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "totalAmount", expression = "java(moneyToDecimal(order.getTotalAmount()))")
    @Mapping(target = "status", expression = "java(order.getStatus().name())")
    OrderDto toDto(Order order);

    @Mapping(target = "unitPrice", expression = "java(moneyToDecimal(item.getUnitPrice()))")
    @Mapping(target = "subtotal", expression = "java(moneyToDecimal(item.getSubtotal()))")
    OrderItemDto toDto(OrderItem item);

    default BigDecimal moneyToDecimal(Money money) {
        return money != null ? money.amount() : null;
    }
}



