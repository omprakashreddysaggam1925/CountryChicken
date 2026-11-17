package com.countrychicken.payment.service;

import com.countrychicken.commerce.dto.*;
import com.countrychicken.commerce.entity.*;
import com.countrychicken.commerce.repository.*;
import com.countrychicken.common.exception.*;
import com.countrychicken.inventory.entity.ReservationStatus;
import com.countrychicken.inventory.service.InventoryService;
import com.countrychicken.payment.gateway.*;
import com.countrychicken.shipping.service.ShippingService;
import com.countrychicken.loyalty.service.LoyaltyService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;

@Service
public class PaymentService {
    private final PaymentRepository payments;
    private final PaymentRefundRepository refunds;
    private final CustomerOrderRepository orders;
    private final PaymentGateway gateway;
    private final InventoryService inventory;
    private final ObjectMapper mapper;
    private final ShippingService shipping;
    private final LoyaltyService loyalty;
    public PaymentService(PaymentRepository payments, PaymentRefundRepository refunds, CustomerOrderRepository orders,
                          PaymentGateway gateway, InventoryService inventory, ObjectMapper mapper, ShippingService shipping, LoyaltyService loyalty) {
        this.payments = payments; this.refunds = refunds; this.orders = orders; this.gateway = gateway;
        this.inventory = inventory; this.mapper = mapper; this.shipping = shipping; this.loyalty = loyalty;
    }
    @Transactional
    public PaymentResponse create(UUID userId, PaymentRequest request) {
        Payment existing = payments.findByIdempotencyKey(request.idempotencyKey()).orElse(null);
        if (existing != null) {
            if (!existing.getOrder().getUser().getId().equals(userId)) throw new ConflictException("Idempotency key is already in use");
            return PaymentResponse.from(existing);
        }
        CustomerOrder order = orders.findByIdAndUserId(request.orderId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order was not found"));
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) throw new ConflictException("Order is not awaiting payment");
        Payment payment = payments.save(new Payment(order, "RAZORPAY", request.idempotencyKey()));
        GatewayOrder gatewayOrder = gateway.createOrder(order.getId().toString(), payment.getAmount(), payment.getCurrency(), request.idempotencyKey());
        payment.providerOrder(gatewayOrder.id());
        return PaymentResponse.from(payment);
    }
    @Transactional
    public void webhook(String payload, String signature) {
        JsonNode root;
        try { root = mapper.readTree(payload); } catch (Exception e) { throw new ConflictException("Invalid payment webhook payload"); }
        String event = root.path("event").asText();
        JsonNode paymentNode = root.path("payload").path("payment").path("entity");
        String providerPaymentId = paymentNode.path("id").asText(null);
        String providerOrderId = paymentNode.path("order_id").asText(null);
        Payment payment = providerOrderId == null ? null : payments.findByProviderOrderId(providerOrderId).orElse(null);
        if (payment == null && providerPaymentId != null) payment = payments.findByProviderPaymentId(providerPaymentId).orElse(null);
        if (payment == null) throw new ResourceNotFoundException("Payment was not found");
        if (event.equals("payment.captured") || event.equals("order.paid")) capture(payment, providerPaymentId);
        else if (event.equals("payment.failed")) fail(payment);
    }
    @Transactional
    public PaymentResponse refund(UUID userId, UUID paymentId, BigDecimal amount) {
        Payment payment = payments.findByIdAndOrderUserId(paymentId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment was not found"));
        if (payment.getStatus() != PaymentStatus.CAPTURED) throw new ConflictException("Only captured payments can be refunded");
        if (amount.compareTo(payment.getAmount()) > 0) throw new ConflictException("Refund exceeds captured amount");
        PaymentRefund refund = refunds.save(new PaymentRefund(payment, amount));
        GatewayRefund providerRefund = gateway.refund(payment.getProviderPaymentId(), amount);
        refund.complete(providerRefund.id());
        if (amount.compareTo(payment.getAmount()) == 0) payment.refund();
        if (amount.compareTo(payment.getAmount()) == 0) loyalty.reverseForRefund(payment.getOrder());
        return PaymentResponse.from(payment);
    }
    private void capture(Payment payment, String providerPaymentId) {
        if (payment.getStatus() == PaymentStatus.CAPTURED) return;
        payment.capture(providerPaymentId);
        payment.getOrder().markPaid();
        payment.getOrder().getItems().forEach(item -> {
            if (item.getStockReservation().getStatus() == ReservationStatus.ACTIVE) inventory.commit(item.getStockReservation().getId());
        });
        shipping.createForPaidOrder(payment.getOrder().getId());
        loyalty.awardForPaidOrder(payment.getOrder());
    }
    private void fail(Payment payment) {
        if (payment.getStatus() == PaymentStatus.FAILED) return;
        payment.fail();
        payment.getOrder().getItems().forEach(item -> {
            if (item.getStockReservation().getStatus() == ReservationStatus.ACTIVE) inventory.release(item.getStockReservation().getId());
        });
    }
}
