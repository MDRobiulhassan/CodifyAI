package com.lovable.codifyAI.controller;

import com.lovable.codifyAI.dto.subscription.CheckoutRequest;
import com.lovable.codifyAI.service.StripePaymentProcessor;
import com.lovable.codifyAI.service.SubscriptionService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class BillingController {

    private final SubscriptionService subscriptionService;
    private final StripePaymentProcessor stripePaymentProcessor;

    @Value("${stripe.webhook.secret-key}")
    private String stripeWebhookSecretKey;

    @GetMapping("/plans")
    public ResponseEntity<?> getAllPlans() {
        return ResponseEntity.ok(subscriptionService.getAllActivePlans());
    }

    @GetMapping("/me/subscription")
    public ResponseEntity<?> getMySubscription() {
        Long userId = 1L;
        return ResponseEntity.ok(subscriptionService.getMySubscription(userId));
    }

    @PostMapping("/payment/checkout")
    public ResponseEntity<?> createCheckoutResponse(@Valid @RequestBody CheckoutRequest request) {
        return ResponseEntity.ok(stripePaymentProcessor.createCheckoutSessionUrl(request));
    }

    @PostMapping("/payment/portal")
    public ResponseEntity<?> openCustomerPortal() {
        Long userId = 1L;
        return ResponseEntity.ok(stripePaymentProcessor.openCustomerPortal(userId));
    }

    @PostMapping("/webhooks/payemnt")
    public ResponseEntity<?> handlePaymentWebhooks(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, stripeWebhookSecretKey);

            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = null;

            if (deserializer.getObject().isPresent()) {
                stripeObject = deserializer.getObject().get();
            } else {
                try {
                    stripeObject = deserializer.deserializeUnsafe();
                    if (stripeObject == null) {
                        log.warn("Failed to deserialize Stripe event data object");
                        return ResponseEntity.ok().build();
                    }
                } catch (Exception e) {
                    log.error("Unsafe deserialization of Stripe event data object failed", e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deserialization Failed");
                }
            }

            Map<String, String> metaData = new HashMap<>();
            if (stripeObject instanceof Session session) {
                metaData = session.getMetadata();
            }

            stripePaymentProcessor.handleWebhookEvent(event.getType(), stripeObject, metaData);
            return ResponseEntity.ok().build();
        } catch (SignatureVerificationException e) {
            throw new RuntimeException(e);
        }
    }
}
