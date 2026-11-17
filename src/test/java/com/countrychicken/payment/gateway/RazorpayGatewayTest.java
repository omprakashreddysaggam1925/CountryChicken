package com.countrychicken.payment.gateway;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

class RazorpayGatewayTest {
    @Test
    void verifiesRazorpayWebhookSignatureAndRejectsTampering() throws Exception {
        String secret = "webhook-secret";
        String payload = "{\"event\":\"payment.captured\"}";
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        StringBuilder signature = new StringBuilder();
        for (byte value : digest) signature.append(String.format("%02x", value));

        RazorpayGateway gateway = new RazorpayGateway("key", "secret", secret, new ObjectMapper());

        assertTrue(gateway.verifyWebhook(payload, signature.toString()));
        assertFalse(gateway.verifyWebhook(payload + "tampered", signature.toString()));
    }
}
