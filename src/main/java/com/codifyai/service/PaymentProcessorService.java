package com.codifyai.service;

import com.codifyai.dto.request.subscription.CheckoutRequest;
import com.codifyai.dto.response.subscription.CheckoutResponse;
import com.codifyai.dto.response.subscription.PortalResponse;
import com.stripe.model.StripeObject;

import java.util.Map;

public interface PaymentProcessorService {
    CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request);

    PortalResponse openCustomerPortal();

    void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metaData);
}
