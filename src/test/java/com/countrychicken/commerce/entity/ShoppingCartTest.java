package com.countrychicken.commerce.entity;

import com.countrychicken.product.entity.ProductVariant;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ShoppingCartTest {
    @Test
    void addingSameVariantIncreasesQuantityAndRefreshesPriceSnapshot() {
        ProductVariant variant = new ProductVariant("CHK-1", 1000, new BigDecimal("12.50"));
        ShoppingCart cart = new ShoppingCart(null);

        cart.addItem(variant, 2);
        variant.update("CHK-1", 1000, new BigDecimal("13.00"), true);
        cart.addItem(variant, 3);

        assertEquals(1, cart.getItems().size());
        assertEquals(5, cart.getItems().getFirst().getQuantity());
        assertEquals(new BigDecimal("13.00"), cart.getItems().getFirst().getPriceSnapshot());
    }
}
