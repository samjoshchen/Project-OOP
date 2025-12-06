package com.martminds.model.payment;

import java.time.LocalDateTime;
import com.martminds.enums.PaymentMethod;
import com.martminds.enums.PaymentStatus;
import com.martminds.exception.PaymentFailedException;
import com.martminds.util.DateTimeUtil;

public abstract class Payment {
    private String paymentId;
    private String userId;
    private String orderId;
    private double amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;

    public Payment(String paymentId, String userId, String orderId, double amount, PaymentMethod method) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.orderId = orderId;
        this.amount = amount;
        this.method = method;
        this.status = PaymentStatus.PENDING;
        this.createdAt = DateTimeUtil.now();
        this.lastUpdatedAt = DateTimeUtil.now(); 
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getUserId() {
        return userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public abstract boolean processPayment() throws PaymentFailedException;

    public boolean refund() throws PaymentFailedException {
        if (status != PaymentStatus.SUCCESS) {
            throw new PaymentFailedException(paymentId, "Cannot refund a payment that was not successful");
        }

        this.status = PaymentStatus.REFUNDED;
        this.lastUpdatedAt = DateTimeUtil.now();
        return true;
    }

    public boolean validateAmount() {
        return amount > 0;
    }

    public void updateStatus(PaymentStatus status) {
        this.status = status;
        this.lastUpdatedAt = DateTimeUtil.now();
    }
}
