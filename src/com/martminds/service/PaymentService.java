package com.martminds.service;

import com.martminds.model.payment.*;
import com.martminds.enums.PaymentMethod;
import com.martminds.enums.PaymentStatus;
import com.martminds.exception.PaymentFailedException;
import com.martminds.exception.InsufficientBalanceException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PaymentService {
    private static PaymentService paymentService;
    private List<Payment> payments;

    private PaymentService() {
        this.payments = new ArrayList<>();
    }

    public static PaymentService getPaymentService() {
        if (paymentService == null) {
            paymentService = new PaymentService();
        }
        return paymentService;
    }

    public Payment createPayment(String userId, String orderId, double amount, PaymentMethod method,
            Object... methodDetails) throws PaymentFailedException {
        String paymentId = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Payment payment;

        switch (method) {
            case CASH:
                if (methodDetails.length > 0 && methodDetails[0] instanceof Double) {
                    payment = new CashPayment(paymentId, userId, orderId, amount, (Double) methodDetails[0]);
                } else {
                    payment = new CashPayment(paymentId, userId, orderId, amount);
                }
                break;

            case CREDIT_CARD:
                if (methodDetails.length < 4) {
                    throw new PaymentFailedException(paymentId, "Credit card details incomplete");
                }
                payment = new CreditCardPayment(
                        paymentId, userId, orderId, amount,
                        (String) methodDetails[0], // cardNumber
                        (String) methodDetails[1], // cardHolder
                        (String) methodDetails[2], // expiry
                        (String) methodDetails[3] // cvv
                );
                break;

            case EWALLET:
                if (methodDetails.length < 1) {
                    throw new PaymentFailedException(paymentId, "E-wallet ID required");
                }
                payment = new EWalletPayment(
                        paymentId, userId, orderId, amount,
                        (String) methodDetails[0] // walletId
                );
                break;

            default:
                throw new PaymentFailedException(paymentId, "Unsupported payment method");
        }

        payments.add(payment);
        return payment;
    }

    public boolean processPayment(String paymentId) throws PaymentFailedException, InsufficientBalanceException {
        Payment payment = getPaymentById(paymentId);

        if (payment == null) {
            throw new PaymentFailedException(paymentId, "Payment not found");
        }

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new PaymentFailedException(paymentId, "Payment already processed");
        }

        if (payment instanceof EWalletPayment) {
            return payment.processPayment();
        }

        return payment.processPayment();
    }

    public boolean refundPayment(String paymentId) throws PaymentFailedException {
        Payment payment = getPaymentById(paymentId);

        if (payment == null) {
            throw new PaymentFailedException(paymentId, "Payment not found");
        }

        return payment.refund();
    }

    public Payment getPaymentById(String paymentId) {
        return payments.stream()
                .filter(p -> p.getPaymentId().equals(paymentId))
                .findFirst()
                .orElse(null);
    }

    public List<Payment> getPaymentsByUserId(String userId) {
        List<Payment> userPayments = new ArrayList<>();

        for (Payment payment : payments) {
            if (payment.getUserId().equals(userId)) {
                userPayments.add(payment);
            }
        }

        return userPayments;
    }

    public List<Payment> getPaymentsByOrderId(String orderId) {
        List<Payment> orderPayments = new ArrayList<>();

        for (Payment payment : payments) {
            if (payment.getOrderId().equals(orderId)) {
                orderPayments.add(payment);
            }
        }

        return orderPayments;
    }

    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        List<Payment> statusPayments = new ArrayList<>();

        for (Payment payment : payments) {
            if (payment.getStatus() == status) {
                statusPayments.add(payment);
            }
        }

        return statusPayments;
    }

    public List<Payment> getAllPayments() {
        return new ArrayList<>(payments);
    }

    public double getTotalPaymentAmount(String userId) {
        double total = 0.0;

        for (Payment payment : payments) {
            if (payment.getUserId().equals(userId) && payment.getStatus() == PaymentStatus.SUCCESS) {
                total += payment.getAmount();
            }
        }

        return total;
    }

    public boolean cancelPayment(String paymentId) throws PaymentFailedException {
        Payment payment = getPaymentById(paymentId);

        if (payment == null) {
            throw new PaymentFailedException(paymentId, "Payment not found");
        }

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new PaymentFailedException(paymentId, "Cannot cancel non-pending payment");
        }

        payment.setStatus(PaymentStatus.CANCELLED);
        return true;
    }

    public List<Payment> getPendingPayments() {
        return getPaymentsByStatus(PaymentStatus.PENDING);
    }

    public List<Payment> getSuccessfulPayments() {
        return getPaymentsByStatus(PaymentStatus.SUCCESS);
    }

    public List<Payment> getFailedPayments() {
        return getPaymentsByStatus(PaymentStatus.FAILED);
    }
}
