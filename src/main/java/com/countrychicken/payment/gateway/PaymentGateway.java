package com.countrychicken.payment.gateway;

import java.math.BigDecimal;

public interface PaymentGateway {
    GatewayOrder createOrder(String receipt, BigDecimal amount, String currency, String idempotencyKey);
    GatewayRefund refund(String providerPaymentId, BigDecimal amount);
}
