package com.lovable.codifyAI.service;

import com.lovable.codifyAI.dto.subscription.CheckoutRequest;
import com.lovable.codifyAI.dto.subscription.CheckoutResponse;
import com.lovable.codifyAI.dto.subscription.PortalResponse;
import com.stripe.model.StripeObject;

import java.util.Map;

public interface StripePaymentProcessor {
    CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request);

    PortalResponse openCustomerPortal(Long userId);

    void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metaData);
}
