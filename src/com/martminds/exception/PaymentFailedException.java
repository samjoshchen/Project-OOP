package com.martminds.exception;

// When transaction fails during payment processing
public class PaymentFailedException extends Exception {
    private String paymentId;
    private String reason;

    public PaymentFailedException(String message) {
        super(message);
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
