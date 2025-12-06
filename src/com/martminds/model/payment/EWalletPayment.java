package com.martminds.model.payment;

import com.martminds.enums.PaymentMethod;
import com.martminds.enums.PaymentStatus;
import com.martminds.exception.PaymentFailedException;

public class EWalletPayment extends Payment {
    private String walletId;
    private String walletProvider;

    public EWalletPayment(String paymentId, String userId, String orderId, double amount, String walletId,
            String walletProvider) {
        super(paymentId, userId, orderId, amount, PaymentMethod.EWALLET);
        this.walletId = walletId;
        this.walletProvider = walletProvider;
    }

    public EWalletPayment(String paymentId, String userId, String orderId, double amount, String walletId) {
        super(paymentId, userId, orderId, amount, PaymentMethod.EWALLET);
        this.walletId = walletId;
        this.walletProvider = "Generic E-Wallet";
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getWalletProvider() {
        return walletProvider;
    }

    public void setWalletProvider(String walletProvider) {
        this.walletProvider = walletProvider;
    }

    @Override
    public boolean processPayment() throws PaymentFailedException {
        if (!validateAmount()) {
            throw new PaymentFailedException(getPaymentId(), "Invalid payment amount");
        }

        if (walletId == null || walletId.trim().isEmpty()) {
            throw new PaymentFailedException(getPaymentId(), "Wallet ID is required");
        }

        if (walletProvider == null || walletProvider.trim().isEmpty()) {
            throw new PaymentFailedException(getPaymentId(), "Wallet provider is required");
        }

        if (!walletId.matches("[a-zA-Z0-9]{6,20}")) {
            throw new PaymentFailedException(getPaymentId(),
                    "Invalid wallet ID format (expected 6-20 alphanumeric characters)");
        }

        setStatus(PaymentStatus.SUCCESS);

        return true;
    }

    @Override
    public boolean refund() throws PaymentFailedException {
        boolean refunded = super.refund();

        if (refunded) {
            System.out.println("E-Wallet refund initiated to " + walletProvider + " ID: " + walletId);
        }

        return refunded;
    }

    // Used for logging, storing, and displaying payment info
    @Override
    public String toString() {
        return String.format("EWalletPayment[ID=%s, Amount=%.2f, Provider=%s, WalletID=%s, Status=%s]",
                getPaymentId(), getAmount(), walletProvider, walletId, getStatus());
    }
}
