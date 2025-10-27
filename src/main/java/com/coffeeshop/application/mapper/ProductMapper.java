package com.coffeeshop.application.mapper;

import com.coffeeshop.application.dto.ProductDto;
import com.coffeeshop.domain.product.Product;
import com.coffeeshop.domain.shared.Money;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "price", expression = "java(moneyToDecimal(product.getPrice()))")
    ProductDto toDto(Product product);

    default BigDecimal moneyToDecimal(Money money) {
        return money != null ? money.amount() : null;
    }

    default Money decimalToMoney(BigDecimal decimal) {
        return decimal != null ? Money.of(decimal) : null;
    }
}



