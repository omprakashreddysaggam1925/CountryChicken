package com.countrychicken.commerce.dto;
import com.countrychicken.commerce.entity.Payment;
import java.math.BigDecimal;
import java.util.UUID;
public record PaymentResponse(UUID id, UUID orderId, String provider, String providerOrderId, BigDecimal amount,
                              String currency, String status) {
    public static PaymentResponse from(Payment p) { return new PaymentResponse(p.getId(), p.getOrder().getId(), p.getProvider(), p.getProviderOrderId(), p.getAmount(), p.getCurrency(), p.getStatus().name()); }
}
