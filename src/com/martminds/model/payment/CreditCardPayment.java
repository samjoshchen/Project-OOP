package com.martminds.model.payment;

import com.martminds.enums.PaymentMethod;
import com.martminds.enums.PaymentStatus;
import com.martminds.exception.PaymentFailedException;
import com.martminds.util.ValidationUtil;

public class CreditCardPayment extends Payment {
    private String cardNumber;
    private String cardHolder;
    private String expiry;
    private String cvv;

    public CreditCardPayment(String paymentId, String userId, String orderId, double amount, String cardNumber,
            String cardHolder, String expiry, String cvv) {
        super(paymentId, userId, orderId, amount, PaymentMethod.CREDIT_CARD);
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
        this.expiry = expiry;
        this.cvv = cvv;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String maskCardNumber() {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "**** **** **** ****";
        }

        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + lastFour;
    }

    @Override
    public boolean processPayment() throws PaymentFailedException {
        if (!validateAmount()) {
            throw new PaymentFailedException(getPaymentId(), "Invalid payment amount");
        }

        if (!ValidationUtil.isValidCreditCard(cardNumber)) {
            throw new PaymentFailedException(getPaymentId(), "Invalid credit card number");
        }

        if (cardHolder == null || cardHolder.trim().isEmpty()) {
            throw new PaymentFailedException(getPaymentId(), "Card holder name is required");
        }

        if (expiry == null || !expiry.matches("\\d{2}/\\d{2}")) {
            throw new PaymentFailedException(getPaymentId(), "Invalid expiry date format (expected MM/YY)");
        }

        if (cvv == null || !cvv.matches("\\d{3,4}")) {
            throw new PaymentFailedException(getPaymentId(), "Invalid CVV (expected 3-4 digits)");
        }

        if (isCardExpired()) {
            throw new PaymentFailedException(getPaymentId(), "Card has expired");
        }

        setStatus(PaymentStatus.SUCCESS);

        return true;
    }

    private boolean isCardExpired() {
        if (expiry == null || !expiry.matches("\\d{2}/\\d{2}")) {
            return true;
        }

        try {
            String[] parts = expiry.split("/");
            int month = Integer.parseInt(parts[0]);
            int year = 2000 + Integer.parseInt(parts[1]);

            java.time.LocalDate now = java.time.LocalDate.now();
            int currentYear = now.getYear();
            int currentMonth = now.getMonthValue();

            if (year < currentYear) {
                return true;
            } else if (year == currentYear && month < currentMonth) {
                return true;
            }

            return false;
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public String toString() {
        return String.format("CreditCardPayment[ID=%s, Amount=%.2f, Card=%s, Holder=%s, Status=%s]",
                getPaymentId(), getAmount(), maskCardNumber(), cardHolder, getStatus());
    }
}
