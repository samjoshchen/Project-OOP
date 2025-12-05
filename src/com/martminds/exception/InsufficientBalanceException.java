package com.martminds.exception;

// When user attempts to perform a transaction with insufficient balance
public class InsufficientBalanceException extends Exception {
    private double requiredAmount;
    private double currentBalance;

    public InsufficientBalanceException(String message) {
        super(message);
    }

    public InsufficientBalanceException(String message, double requiredAmount, double currentBalance) {
        super(message);
        this.requiredAmount = requiredAmount;
        this.currentBalance = currentBalance;
    }

    public double getRequiredAmount() {
        return requiredAmount;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public double getShortfall() {
        return requiredAmount - currentBalance;
    }
}
