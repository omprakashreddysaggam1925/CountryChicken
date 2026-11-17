package com.countrychicken.reporting.service;

import com.countrychicken.commerce.entity.OrderStatus;
import com.countrychicken.commerce.repository.CustomerOrderRepository;
import com.countrychicken.product.entity.ProductStatus;
import com.countrychicken.product.repository.ProductRepository;
import com.countrychicken.reporting.dto.DashboardResponse;
import com.countrychicken.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ReportingService {
    private final UserRepository users;
    private final ProductRepository products;
    private final CustomerOrderRepository orders;
    public ReportingService(UserRepository users, ProductRepository products, CustomerOrderRepository orders) {
        this.users = users; this.products = products; this.orders = orders;
    }
    public DashboardResponse dashboard() {
        return new DashboardResponse(
                users.count(),
                products.countByStatus(ProductStatus.ACTIVE),
                orders.countByStatus(OrderStatus.PAID) + orders.countByStatus(OrderStatus.PROCESSING)
                        + orders.countByStatus(OrderStatus.SHIPPED) + orders.countByStatus(OrderStatus.DELIVERED),
                orders.countByStatus(OrderStatus.DELIVERED));
    }
}
