package com.coffeeshop.domain.shared;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyTest {
    @Test
    void shouldCreateMoneyWithValidAmount() {
        Money money = Money.of("10.50");
        assertThat(money.amount()).isEqualByComparingTo(new BigDecimal("10.50"));
    }

    @Test
    void shouldRoundToTwoDecimalPlaces() {
        Money money = Money.of("10.567");
        assertThat(money.amount()).isEqualByComparingTo(new BigDecimal("10.57"));
    }

    @Test
    void shouldThrowExceptionForNegativeAmount() {
        assertThatThrownBy(() -> Money.of("-10.00"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be negative");
    }

    @Test
    void shouldThrowExceptionForNullAmount() {
        assertThatThrownBy(() -> new Money(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldAddMoney() {
        Money money1 = Money.of("10.50");
        Money money2 = Money.of("5.25");
        Money result = money1.add(money2);

        assertThat(result.amount()).isEqualByComparingTo(new BigDecimal("15.75"));
    }

    @Test
    void shouldMultiplyByQuantity() {
        Money money = Money.of("10.00");
        Money result = money.multiply(3);

        assertThat(result.amount()).isEqualByComparingTo(new BigDecimal("30.00"));
    }

    @Test
    void shouldCompareMoneyAmounts() {
        Money money1 = Money.of("15.00");
        Money money2 = Money.of("10.00");

        assertThat(money1.isGreaterThan(money2)).isTrue();
        assertThat(money2.isGreaterThan(money1)).isFalse();
    }
}



