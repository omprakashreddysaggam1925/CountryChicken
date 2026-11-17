package com.countrychicken.inventory.entity;

import com.countrychicken.product.entity.ProductVariant;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class InventoryItemTest {

    @Test
    void reservationAndCommitReduceAvailableAndOnHandStock() {
        InventoryItem item = new InventoryItem(
                new ProductVariant("CC-1KG", 1_000, new BigDecimal("12.50")), 2);

        item.adjustOnHand(10);
        item.reserve(4);
        assertThat(item.getOnHand()).isEqualTo(10);
        assertThat(item.getReserved()).isEqualTo(4);
        assertThat(item.getAvailable()).isEqualTo(6);

        item.commit(4);
        assertThat(item.getOnHand()).isEqualTo(6);
        assertThat(item.getReserved()).isZero();
    }

    @Test
    void reservationCannotExceedAvailableStock() {
        InventoryItem item = new InventoryItem(
                new ProductVariant("CC-2KG", 2_000, new BigDecimal("20.00")), 1);
        item.adjustOnHand(3);

        assertThatIllegalArgumentException().isThrownBy(() -> item.reserve(4));
    }
}
