package com.martminds.view;

import com.martminds.enums.PaymentMethod;
import com.martminds.model.payment.Payment;
import com.martminds.util.Input;
import java.util.HashMap;
import java.util.Map;

public class PaymentView {
    
    public PaymentMethod selectPaymentMethod() {
        System.out.println("\nSelect Payment Method\n");
        System.out.println("1. Cash");
        System.out.println("2. E-Wallet");
        System.out.println("3. Credit Card");
        
        int choice = Input.promptInt("Choose payment method: ");
        
        switch (choice) {
            case 1: return PaymentMethod.CASH;
            case 2: return PaymentMethod.EWALLET;
            case 3: return PaymentMethod.CREDIT_CARD;
            default: return PaymentMethod.CASH;
        }
    }
    
    public Map<String, String> getPaymentDetails(PaymentMethod method) {
        Map<String, String> details = new HashMap<>();
        
        switch (method) {
            case EWALLET:
                details.put("walletId", Input.promptString("E-Wallet ID: "));
                break;
            case CREDIT_CARD:
                details.put("cardNumber", Input.promptString("Card Number: "));
                details.put("cardHolder", Input.promptString("Card Holder Name: "));
                details.put("expiry", Input.promptString("Expiry (MM/YY): "));
                details.put("cvv", Input.promptString("CVV: "));
                break;
            case CASH:
            default:
                break;
        }
        
        return details;
    }
    
    public void displayPaymentSuccess(Payment payment) {
        System.out.println("\nPayment Successful\n");
        System.out.println("Payment ID : " + payment.getPaymentId());
        System.out.println("Amount     : " + payment.getAmount());
        System.out.println("Method     : " + payment.getMethod());
        System.out.println("Status     : " + payment.getStatus());
        System.out.println();
    }
    
    public void displayPaymentFailed(String reason) {
        System.err.println("\nPayment Failed\n");
        System.err.println("Reason: " + reason);
        System.err.println();
    }
    
    public boolean confirmPayment(double amount) {
        System.out.println("\nPayment Confirmation\n");
        System.out.printf("Total Amount: %.2f\n", amount);
        String confirm = Input.promptString("Proceed with payment? (yes/no): ");
        return confirm.equalsIgnoreCase("yes") || confirm.equalsIgnoreCase("y");
    }
}
