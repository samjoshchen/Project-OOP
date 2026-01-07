package com.martminds.service;

import com.martminds.model.payment.Payment;
import com.martminds.model.payment.CashPayment;
import com.martminds.model.payment.EWalletPayment;
import com.martminds.model.payment.CreditCardPayment;
import com.martminds.enums.PaymentMethod;
import com.martminds.enums.PaymentStatus;
import com.martminds.exception.PaymentFailedException;
import com.martminds.exception.InsufficientBalanceException;
import com.martminds.util.FileHandler;
import com.martminds.util.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PaymentService {
    private static PaymentService instance;
    private List<Payment> payments;
    private static final String PAYMENT_FILE = "payments.csv";

    private PaymentService() {
        this.payments = new ArrayList<>();
        loadFromFile();
    }

    public static PaymentService getInstance() {
        if (instance == null) {
            instance = new PaymentService();
        }
        return instance;
    }

    private void loadFromFile() {
        List<String> lines = FileHandler.readFile(PAYMENT_FILE);

        for (String line : lines) {
            if (line.trim().isEmpty())
                continue;

            try {
                String[] fields = FileHandler.parseCSVLine(line);
                if (fields.length < 6)
                    continue;

                String paymentId = fields[0];
                String userId = fields[1];
                String orderId = fields[2];
                double amount = Double.parseDouble(fields[3]);
                PaymentMethod method = PaymentMethod.valueOf(fields[4]);
                PaymentStatus status = PaymentStatus.valueOf(fields[5]);

                Payment payment;
                switch (method) {
                    case CASH:
                        String citizenId = fields.length > 6 ? fields[6] : "";
                        payment = new CashPayment(paymentId, userId, orderId, amount, citizenId);
                        break;
                    case EWALLET:
                        String walletId = fields.length > 6 ? fields[6] : userId;
                        payment = new EWalletPayment(paymentId, userId, orderId, amount, walletId);
                        break;
                    case CREDIT_CARD:
                        String cardNumber = fields.length > 9 ? fields[6] : "";
                        String cardHolder = fields.length > 9 ? fields[7] : "";
                        String expiry = fields.length > 9 ? fields[8] : "";
                        String cvv = fields.length > 9 ? fields[9] : "";
                        payment = new CreditCardPayment(paymentId, userId, orderId, amount,
                                cardNumber, cardHolder, expiry, cvv);
                        break;
                    default:
                        continue;
                }

                payment.setStatus(status);
                payments.add(payment);
            } catch (Exception e) {
                Logger.error("Error parsing payment line: " + line + " - " + e.getMessage());
            }
        }

        Logger.info("Loaded " + payments.size() + " payments from file");
    }

    private void saveToFile() {
        List<String> lines = new ArrayList<>();

        for (Payment payment : payments) {
            String line;
            if (payment instanceof CashPayment) {
                CashPayment cp = (CashPayment) payment;
                line = FileHandler.formatCSVLine(
                        payment.getPaymentId(),
                        payment.getUserId(),
                        payment.getOrderId(),
                        String.valueOf(payment.getAmount()),
                        payment.getMethod().toString(),
                        payment.getStatus().toString(),
                        cp.getCitizenId() != null ? cp.getCitizenId() : "");
            } else if (payment instanceof EWalletPayment) {
                EWalletPayment ep = (EWalletPayment) payment;
                line = FileHandler.formatCSVLine(
                        payment.getPaymentId(),
                        payment.getUserId(),
                        payment.getOrderId(),
                        String.valueOf(payment.getAmount()),
                        payment.getMethod().toString(),
                        payment.getStatus().toString(),
                        ep.getWalletId());
            } else if (payment instanceof CreditCardPayment) {
                CreditCardPayment ccp = (CreditCardPayment) payment;
                line = FileHandler.formatCSVLine(
                        payment.getPaymentId(),
                        payment.getUserId(),
                        payment.getOrderId(),
                        String.valueOf(payment.getAmount()),
                        payment.getMethod().toString(),
                        payment.getStatus().toString(),
                        ccp.getCardNumber(),
                        ccp.getCardHolder(),
                        ccp.getExpiry(),
                        ccp.getCvv());
            } else {
                line = FileHandler.formatCSVLine(
                        payment.getPaymentId(),
                        payment.getUserId(),
                        payment.getOrderId(),
                        String.valueOf(payment.getAmount()),
                        payment.getMethod().toString(),
                        payment.getStatus().toString());
            }
            lines.add(line);
        }

        FileHandler.writeFile(PAYMENT_FILE, lines);
    }

    public Payment createPayment(String userId, String orderId, double amount, PaymentMethod method,
            Object... methodDetails) throws PaymentFailedException {
        String paymentId = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Payment payment;

        switch (method) {
            case CASH:

                if (methodDetails.length < 1) {
                    throw new PaymentFailedException(paymentId, "Citizen ID is required for cash payment");
                }
                payment = new CashPayment(
                        paymentId, userId, orderId, amount,
                        (String) methodDetails[0]);
                break;

            case CREDIT_CARD:

                if (methodDetails.length < 4) {
                    throw new PaymentFailedException(paymentId, "Complete card details required");
                }
                payment = new CreditCardPayment(
                        paymentId, userId, orderId, amount,
                        (String) methodDetails[0],
                        (String) methodDetails[1],
                        (String) methodDetails[2],
                        (String) methodDetails[3]);
                break;

            case EWALLET:

                if (methodDetails.length < 1) {
                    throw new PaymentFailedException(paymentId, "Wallet ID is required");
                }
                payment = new EWalletPayment(
                        paymentId, userId, orderId, amount,
                        (String) methodDetails[0]);
                break;

            default:
                throw new PaymentFailedException(paymentId, "Unsupported payment method: " + method);
        }

        payments.add(payment);
        saveToFile();
        return payment;
    }

    public boolean processPayment(String paymentId) throws PaymentFailedException, InsufficientBalanceException {
        Payment payment = getPaymentById(paymentId);

        if (payment == null) {
            throw new PaymentFailedException(paymentId, "Payment not found");
        }

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new PaymentFailedException(paymentId,
                    "Payment already processed with status: " + payment.getStatus());
        }

        if (payment instanceof CashPayment) {
            CashPayment cashPayment = (CashPayment) payment;
            if (cashPayment.getReceivedAmount() == 0) {
                throw new PaymentFailedException(paymentId,
                        "Received amount must be set before processing cash payment");
            }
        }

        boolean result = payment.processPayment();
        saveToFile();
        return result;
    }

    public boolean refundPayment(String paymentId) throws PaymentFailedException {
        Payment payment = getPaymentById(paymentId);

        if (payment == null) {
            throw new PaymentFailedException(paymentId, "Payment not found");
        }

        boolean result = payment.refund();
        saveToFile();
        return result;
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
        saveToFile();
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
