package com.countrychicken.payment.gateway;

import com.countrychicken.common.exception.ConflictException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class RazorpayGateway implements PaymentGateway {
    private final String keyId;
    private final String keySecret;
    private final String webhookSecret;
    private final ObjectMapper mapper;
    private final HttpClient client = HttpClient.newHttpClient();

    public RazorpayGateway(@Value("${payment.razorpay.key-id:}") String keyId,
                           @Value("${payment.razorpay.key-secret:}") String keySecret,
                           @Value("${payment.razorpay.webhook-secret:}") String webhookSecret,
                           ObjectMapper mapper) {
        this.keyId = keyId; this.keySecret = keySecret; this.webhookSecret = webhookSecret; this.mapper = mapper;
    }
    @Override
    public GatewayOrder createOrder(String receipt, BigDecimal amount, String currency, String idempotencyKey) {
        requireConfigured();
        try {
            String body = mapper.createObjectNode().put("amount", amount.movePointRight(2).longValueExact())
                    .put("currency", currency).put("receipt", receipt).put("notes", idempotencyKey).toString();
            JsonNode response = send("POST", "/orders", body, idempotencyKey);
            return new GatewayOrder(response.path("id").asText(), response.path("status").asText());
        } catch (Exception e) {
            throw new ConflictException("Razorpay order creation failed");
        }
    }
    @Override
    public GatewayRefund refund(String providerPaymentId, BigDecimal amount) {
        requireConfigured();
        try {
            String body = mapper.createObjectNode().put("amount", amount.movePointRight(2).longValueExact()).toString();
            JsonNode response = send("POST", "/payments/" + providerPaymentId + "/refund", body, null);
            return new GatewayRefund(response.path("id").asText(), response.path("status").asText());
        } catch (Exception e) {
            throw new ConflictException("Razorpay refund failed");
        }
    }
    public boolean verifyWebhook(String payload, String signature) {
        if (webhookSecret.isBlank() || signature == null || signature.isBlank()) return false;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] expected = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            byte[] provided = hex(signature);
            return java.security.MessageDigest.isEqual(expected, provided);
        } catch (Exception e) {
            return false;
        }
    }
    private JsonNode send(String method, String path, String body, String idempotencyKey) throws Exception {
        String auth = Base64.getEncoder().encodeToString((keyId + ":" + keySecret).getBytes(StandardCharsets.UTF_8));
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("https://api.razorpay.com/v1" + path))
                .header("Authorization", "Basic " + auth).header("Content-Type", "application/json")
                .method(method, HttpRequest.BodyPublishers.ofString(body));
        if (idempotencyKey != null) builder.header("X-Payout-Idempotency", idempotencyKey);
        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) throw new IllegalStateException("Gateway response " + response.statusCode());
        return mapper.readTree(response.body());
    }
    private byte[] hex(String value) {
        if ((value.length() & 1) != 0) return new byte[0];
        byte[] result = new byte[value.length() / 2];
        for (int i = 0; i < result.length; i++) result[i] = (byte) Integer.parseInt(value.substring(i * 2, i * 2 + 2), 16);
        return result;
    }
    private void requireConfigured() {
        if (keyId.isBlank() || keySecret.isBlank()) throw new ConflictException("Razorpay credentials are not configured");
    }
}
