package com.martminds.model.payment;

import com.martminds.enums.PaymentMethod;
import com.martminds.enums.PaymentStatus;
import com.martminds.exception.PaymentFailedException;

public class CashPayment extends Payment {
    private double receivedAmount;
    private double changeAmount;

    public CashPayment(String paymentId, String userId, String orderId, double amount) {
        super(paymentId, userId, orderId, amount, PaymentMethod.CASH);
        this.receivedAmount = 0;
        this.changeAmount = 0;
    }

    public CashPayment(String paymentId, String userId, String orderId, double amount, double receivedAmount) {
        super(paymentId, userId, orderId, amount, PaymentMethod.CASH);
        this.receivedAmount = receivedAmount;
        this.changeAmount = calculateChange();
    }

    public double getReceivedAmount() {
        return receivedAmount;
    }

    public void setReceivedAmount(double receivedAmount) {
        this.receivedAmount = receivedAmount;
        this.changeAmount = calculateChange();
    }

    public double getChangeAmount() {
        return changeAmount;
    }

    public double calculateChange() {
        if (receivedAmount >= getAmount()) {
            return receivedAmount - getAmount();
        }
        return 0;
    }

    @Override
    public boolean processPayment() throws PaymentFailedException {
        if (!validateAmount()) {
            throw new PaymentFailedException(getPaymentId(), "Invalid payment amount");
        }

        if (receivedAmount < getAmount()) {
            throw new PaymentFailedException(getPaymentId(),
                    String.format("Insufficient cash received. Required: %.2f, Received: %.2f",
                            getAmount(), receivedAmount));
        }

        this.changeAmount = calculateChange();

        setStatus(PaymentStatus.SUCCESS);

        return true;
    }

    // Used for logging, storing, and displaying payment info
    @Override
    public String toString() {
        return String.format("CashPayment[ID=%s, Amount=%.2f, Received=%.2f, Change=%.2f, Status=%s]",
                getPaymentId(), getAmount(), receivedAmount, changeAmount, getStatus());
    }
}
