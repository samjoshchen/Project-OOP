package com.martminds.exception;

public class PaymentFailedException extends Exception {
    private String paymentId;
    private String reason;

    public PaymentFailedException(String message) {
        super(message);
    }

    public PaymentFailedException(String paymentId, String reason) {
        super("Payment failed for ID: " + paymentId + " - Reason: " + reason);
        this.paymentId = paymentId;
        this.reason = reason;
    }

    public PaymentFailedException(String message, String paymentId, String reason) {
        super(message);
        this.paymentId = paymentId;
        this.reason = reason;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getReason() {
        return reason;
    }
}
