package com.countrychicken.commerce.entity;

import com.countrychicken.inventory.entity.StockReservation;
import com.countrychicken.category.entity.Category;
import com.countrychicken.product.entity.Product;
import com.countrychicken.product.entity.ProductVariant;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class CustomerOrderTest {
    @Test
    void orderAccumulatesImmutableLineTotalsAndCanCancelWhilePending() {
        ProductVariant variant = new ProductVariant("CHK-1", 1000, new BigDecimal("12.50"));
        Product product = new Product(new Category("Chicken", "chicken", null, null), "Country Chicken", "country-chicken", null,
                com.countrychicken.product.entity.ProductStatus.ACTIVE);
        product.addVariant(variant);
        CustomerOrder order = new CustomerOrder(null, null, "INR");
        StockReservation reservation = new StockReservation(null, 2, java.time.Instant.now().plusSeconds(600));

        order.addItem(new OrderItem(order, variant, 2, reservation));
        variant.update("CHK-1", 1000, new BigDecimal("15.00"), true);

        assertEquals(new BigDecimal("25.00"), order.getSubtotal());
        assertEquals(new BigDecimal("12.50"), order.getItems().getFirst().getUnitPrice());
        order.cancel();
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }
}
