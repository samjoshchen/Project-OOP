package com.martminds.model.payment;

import com.martminds.enums.PaymentMethod;
import com.martminds.enums.PaymentStatus;
import com.martminds.exception.PaymentFailedException;
import com.martminds.model.user.User;
import com.martminds.service.UserService;

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
        this.walletProvider = "MartMinds Balance";
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

        if ("MartMinds Balance".equalsIgnoreCase(this.walletProvider)) {

            User user = UserService.getInstance().findUserById(getUserId());
            if (user == null) {
                throw new PaymentFailedException(getPaymentId(), "User not found");
            }

            if (user.getBalance() < getAmount()) {
                throw new PaymentFailedException(getPaymentId(),
                        String.format("Insufficient MartMinds Balance. Required: Rp %.0f, Available: Rp %.0f",
                                getAmount(), user.getBalance()));
            }

            boolean withdrawn = user.withdrawFunds(getAmount());
            if (!withdrawn) {
                throw new PaymentFailedException(getPaymentId(), "Failed to deduct balance");
            }
        } else {

            System.out.println("Processing external e-wallet payment via " + walletProvider + "...");
            System.out.println("Payment successful via " + walletProvider + " (simulated)");
        }

        setStatus(PaymentStatus.SUCCESS);
        return true;
    }

    @Override
    public boolean refund() throws PaymentFailedException {

        if ("MartMinds Balance".equalsIgnoreCase(this.walletProvider)) {
            User user = UserService.getInstance().findUserById(getUserId());
            if (user == null) {
                throw new PaymentFailedException(getPaymentId(), "User not found for refund");
            }

            user.addFunds(getAmount());
            System.out.println("Refund of Rp " + String.format("%,.0f", getAmount()) +
                    " added back to MartMinds Balance");
        } else {

            System.out.println("Refund processed via " + walletProvider + " (simulated)");
        }

        setStatus(PaymentStatus.CANCELLED);
        return true;
    }

    @Override
    public String toString() {
        return String.format("EWalletPayment[ID=%s, Amount=%.2f, Provider=%s, WalletID=%s, Status=%s]",
                getPaymentId(), getAmount(), walletProvider, walletId, getStatus());
    }
}
