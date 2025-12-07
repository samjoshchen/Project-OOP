package com.martminds.controller;

import java.util.List;

import com.martminds.enums.PaymentMethod;
import com.martminds.enums.PaymentStatus;
import com.martminds.model.payment.Payment;
import com.martminds.model.user.User;
import com.martminds.service.PaymentService;
import com.martminds.service.UserService;
import com.martminds.util.Session;
import com.martminds.util.ValidationUtil;
import com.martminds.exception.PaymentFailedException;
import com.martminds.exception.InsufficientBalanceException;

public class PaymentController {

    public Payment createPayment(String userId, String orderId, double amount,
            PaymentMethod method, String... paymentDetails) throws PaymentFailedException {
        Session.getInstance().requireLogin();

        if (!ValidationUtil.isNotEmpty(userId)) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }
        if (!ValidationUtil.isNotEmpty(orderId)) {
            throw new IllegalArgumentException("Order ID cannot be empty");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than 0");
        }
        if (method == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }

        User user = UserService.getInstance().findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        String currentUserId = Session.getInstance().getCurrentUserId();
        if (!currentUserId.equals(userId) && !Session.getInstance().isAdmin()) {
            throw new IllegalStateException("You are not authorized to create payment for this user");
        }

        validatePaymentDetails(method, paymentDetails);

        Payment payment = PaymentService.getInstance().createPayment(
                userId, orderId, amount, method, (Object[]) paymentDetails);

        return payment;
    }

    public boolean processPayment(String paymentId) throws PaymentFailedException, InsufficientBalanceException {
        Session.getInstance().requireLogin();

        if (!ValidationUtil.isNotEmpty(paymentId)) {
            throw new IllegalArgumentException("Payment ID cannot be empty");
        }

        Payment payment = PaymentService.getInstance().getPaymentById(paymentId);
        if (payment == null) {
            throw new IllegalArgumentException("Payment not found: " + paymentId);
        }

        String currentUserId = Session.getInstance().getCurrentUserId();
        if (!currentUserId.equals(payment.getUserId()) && !Session.getInstance().isAdmin()) {
            throw new IllegalStateException("You are not authorized to process this payment");
        }

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Payment has already been processed successfully");
        }
        if (payment.getStatus() == PaymentStatus.CANCELLED) {
            throw new IllegalStateException("Payment has been cancelled");
        }

        return PaymentService.getInstance().processPayment(paymentId);
    }

    public Payment getPaymentById(String paymentId) {
        Session.getInstance().requireLogin();

        if (!ValidationUtil.isNotEmpty(paymentId)) {
            throw new IllegalArgumentException("Payment ID cannot be empty");
        }

        Payment payment = PaymentService.getInstance().getPaymentById(paymentId);
        if (payment == null) {
            throw new IllegalArgumentException("Payment not found: " + paymentId);
        }

        String currentUserId = Session.getInstance().getCurrentUserId();
        if (!currentUserId.equals(payment.getUserId()) && !Session.getInstance().isAdmin()) {
            throw new IllegalStateException("You are not authorized to view this payment");
        }

        return payment;
    }

    public List<Payment> getPaymentsByUser(String userId) {
        Session.getInstance().requireLogin();

        if (!ValidationUtil.isNotEmpty(userId)) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }

        String currentUserId = Session.getInstance().getCurrentUserId();
        if (!currentUserId.equals(userId) && !Session.getInstance().isAdmin()) {
            throw new IllegalStateException("You are not authorized to view payments for this user");
        }

        return PaymentService.getInstance().getPaymentsByUserId(userId);
    }

    public List<Payment> getPaymentsByOrder(String orderId) {
        Session.getInstance().requireLogin();

        if (!ValidationUtil.isNotEmpty(orderId)) {
            throw new IllegalArgumentException("Order ID cannot be empty");
        }

        return PaymentService.getInstance().getPaymentsByOrderId(orderId);
    }

    public List<Payment> getAllPayments() {
        Session.getInstance().requireAdmin();

        return PaymentService.getInstance().getAllPayments();
    }

    public boolean cancelPayment(String paymentId) {
        Session.getInstance().requireLogin();

        if (!ValidationUtil.isNotEmpty(paymentId)) {
            throw new IllegalArgumentException("Payment ID cannot be empty");
        }

        Payment payment = PaymentService.getInstance().getPaymentById(paymentId);
        if (payment == null) {
            throw new IllegalArgumentException("Payment not found: " + paymentId);
        }

        String currentUserId = Session.getInstance().getCurrentUserId();
        if (!currentUserId.equals(payment.getUserId()) && !Session.getInstance().isAdmin()) {
            throw new IllegalStateException("You are not authorized to cancel this payment");
        }

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Cannot cancel a successful payment");
        }
        if (payment.getStatus() == PaymentStatus.CANCELLED) {
            throw new IllegalStateException("Payment is already cancelled");
        }

        payment.setStatus(PaymentStatus.CANCELLED);
        return true;
    }

    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        Session.getInstance().requireAdmin();

        if (status == null) {
            throw new IllegalArgumentException("Payment status cannot be null");
        }

        return PaymentService.getInstance().getPaymentsByStatus(status);
    }

    public double getTotalPaymentAmount(String userId) {
        Session.getInstance().requireLogin();

        if (!ValidationUtil.isNotEmpty(userId)) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }

        String currentUserId = Session.getInstance().getCurrentUserId();
        if (!currentUserId.equals(userId) && !Session.getInstance().isAdmin()) {
            throw new IllegalStateException("You are not authorized to view payment totals for this user");
        }

        return PaymentService.getInstance().getTotalPaymentAmount(userId);
    }

    private void validatePaymentDetails(PaymentMethod method, String... details) {
        switch (method) {
            case CREDIT_CARD:
                if (details.length < 3) {
                    throw new IllegalArgumentException("Credit card payment requires: card number, CVV, expiry date");
                }
                if (!ValidationUtil.isValidCreditCard(details[0])) {
                    throw new IllegalArgumentException("Invalid credit card number");
                }
                if (!details[1].matches("^[0-9]{3,4}$")) {
                    throw new IllegalArgumentException("Invalid CVV format");
                }
                if (!details[2].matches("^(0[1-9]|1[0-2])/[0-9]{2}$")) {
                    throw new IllegalArgumentException("Invalid expiry date format (MM/YY)");
                }
                break;

            case EWALLET:
                if (details.length < 2) {
                    throw new IllegalArgumentException("E-wallet payment requires: provider name, wallet ID");
                }
                if (!ValidationUtil.isNotEmpty(details[0])) {
                    throw new IllegalArgumentException("E-wallet provider name cannot be empty");
                }
                if (!ValidationUtil.isNotEmpty(details[1])) {
                    throw new IllegalArgumentException("Wallet ID cannot be empty");
                }
                break;

            case CASH:
                break;

            default:
                throw new IllegalArgumentException("Unsupported payment method: " + method);
        }
    }
}