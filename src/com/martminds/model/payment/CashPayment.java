package com.martminds.model.payment;

import com.martminds.enums.PaymentMethod;
import com.martminds.enums.PaymentStatus;
import com.martminds.exception.PaymentFailedException;

public class CashPayment extends Payment {
    private double receivedAmount;
    private double changeAmount;
    private String citizenId;

    public CashPayment(String paymentId, String userId, String orderId, double amount, String citizenId) {
        super(paymentId, userId, orderId, amount, PaymentMethod.CASH);
        this.receivedAmount = 0;
        this.changeAmount = 0;
        this.citizenId = citizenId;
    }

    public CashPayment(String paymentId, String userId, String orderId, double amount,
            double receivedAmount, String citizenId) {
        super(paymentId, userId, orderId, amount, PaymentMethod.CASH);
        this.receivedAmount = receivedAmount;
        this.changeAmount = calculateChange();
        this.citizenId = citizenId;
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

    public String getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(String citizenId) {
        this.citizenId = citizenId;
    }

    public String maskCitizenId() {
        if (citizenId == null || citizenId.length() < 6) {
            return "******";
        }
        String lastFour = citizenId.substring(citizenId.length() - 4);
        return "******" + lastFour;
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

        if (citizenId == null || citizenId.trim().isEmpty()) {
            throw new PaymentFailedException(getPaymentId(), "Citizen ID is required for cash payment");
        }

        if (!citizenId.matches("\\d{16}")) {
            throw new PaymentFailedException(getPaymentId(),
                    "Invalid Citizen ID format (expected 16 digits)");
        }

        if (receivedAmount < getAmount()) {
            throw new PaymentFailedException(getPaymentId(),
                    String.format("Insufficient cash received. Required: Rp %.0f, Received: Rp %.0f",
                            getAmount(), receivedAmount));
        }

        this.changeAmount = calculateChange();
        setStatus(PaymentStatus.SUCCESS);

        return true;
    }

    @Override
    public String toString() {
        return String.format(
                "CashPayment[ID=%s, Amount=Rp%.0f, Received=Rp%.0f, Change=Rp%.0f, CitizenID=%s, Status=%s]",
                getPaymentId(), getAmount(), receivedAmount, changeAmount, maskCitizenId(), getStatus());
    }
}
